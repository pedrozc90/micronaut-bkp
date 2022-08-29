package com.pedrozc90.users;

import com.pedrozc90.users.models.Profile;
import com.pedrozc90.users.models.User;
import com.pedrozc90.users.repo.UserRepository;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

@MicronautTest
public class UserRepositoryTest {

    @Inject
    private UserRepository userRepository;

    @Test
    public void testFindById() {
        final long userId = 1;

        final Optional<User> userOpt = userRepository.findById(userId);
        Assertions.assertNotNull(userOpt);
        Assertions.assertTrue(userOpt.isPresent());
        Assertions.assertNotNull(userOpt.get());

        final User user = userOpt.get();
        Assertions.assertEquals(userId, user.getId());
        Assertions.assertEquals("master", user.getUsername());
        Assertions.assertEquals("admin@email.com", user.getEmail());
        Assertions.assertEquals(Profile.MASTER, user.getProfile());
        Assertions.assertNotNull(user.getAudit());
    }

    @Test
    public void failFindById() {
        final long userId = 1_000;
        Assertions.assertThrows(Exception.class, () -> {
            final User user = userRepository.findByIdOrThrowException(userId);
            Assertions.assertNull(user);
        });
    }

}
