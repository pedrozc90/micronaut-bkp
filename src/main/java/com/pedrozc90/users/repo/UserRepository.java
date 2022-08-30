package com.pedrozc90.users.repo;

import com.pedrozc90.core.data.CrudRepository;
import com.pedrozc90.core.exceptions.ApplicationException;
import com.pedrozc90.users.models.QUser;
import com.pedrozc90.users.models.User;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.runtime.ApplicationConfiguration;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Singleton;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Singleton
public class UserRepository extends CrudRepository<User> {

    private final ApplicationConfiguration config;

    public UserRepository(final ApplicationConfiguration config) {
        super("users", User.class, QUser.user);
        this.config = config;
    }

    @Override
    public User findByIdOrThrowException(@NotNull @NonNull final Long id) throws ApplicationException {
        final Optional<User> opt = findById(id);
        if (opt.isEmpty()) {
            throw ApplicationException.of("User (id: %d) not found.", id).notFound();
        }
        return opt.get();
    }

    @ReadOnly
    public Optional<User> findByCredentials(final String username, final String password) {
        return findOne(QUser.user.username.eq(username).and(QUser.user.password.eq(password)));
    }

}