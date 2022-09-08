package com.pedrozc90.users.repo;

import com.pedrozc90.core.data.EntityRepository;
import com.pedrozc90.users.models.QUser;
import com.pedrozc90.users.models.User;
import com.pedrozc90.users.models.UserRegistration;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.runtime.ApplicationConfiguration;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Singleton;
import org.apache.commons.codec.digest.DigestUtils;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Singleton
public class UserRepository extends EntityRepository<User, Long> {

    private final ApplicationConfiguration config;

    public UserRepository(final ApplicationConfiguration config) {
        super("users", User.class, QUser.user);
        this.config = config;
    }

    @ReadOnly
    @SuppressWarnings("MicronautDataMethodInconsistencyInspection")
    public Optional<User> findByCredentials(final String username, final String password) {
        return findOne(QUser.user.username.eq(username).and(QUser.user.password.eq(password)));
    }

    @ReadOnly
    public Optional<User> findByUsername(@NotNull @NonNull final String username) {
        return findOne(QUser.user.username.eq(username));
    }

    @ReadOnly
    public boolean validateEmail(@NotNull @NonNull final String email) {
        return exists(QUser.user.email.equalsIgnoreCase(email));
    }

    @ReadOnly
    public boolean validateUsername(@NotNull @NonNull final String username) {
        return exists(QUser.user.username.equalsIgnoreCase(username));
    }

    @Transactional
    public User register(@NotNull @NonNull final UserRegistration data) {
        final String passwordHashed = DigestUtils.md5Hex(data.getPassword());

        final User user = new User();
        user.setEmail(data.getEmail());
        user.setUsername(data.getUsername());
        user.setPassword(passwordHashed);
        // user.setPasswordConfirm(data.getPasswordConfirm());
        return save(user);
    }

}