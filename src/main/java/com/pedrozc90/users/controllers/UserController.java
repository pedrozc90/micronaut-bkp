package com.pedrozc90.users.controllers;

import com.pedrozc90.core.exceptions.ApplicationException;
import com.pedrozc90.core.models.ResultContent;
import com.pedrozc90.users.models.*;
import com.pedrozc90.users.repo.UserRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.PersistenceException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

@Slf4j
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.IO)
@Controller("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Get("/")
    public List<User> fetch() {
        return userRepository.builder()
            .orderBy(QUser.user.id.asc())
            .select(QUser.user)
            .fetch();
    }

    @Post("/")
    public HttpResponse<?> save(@Valid @Body final UserRegistration data) {
        try {
            if (!StringUtils.equals(data.getPassword(), data.getPasswordConfirm())) {
                throw new ApplicationException("Password and password confirm do not match.", HttpStatus.BAD_REQUEST);
            }

            final User tmp = UserRegistration.transform(data);
            final User user = userRepository.save(tmp);
            return HttpResponse
                .created(user)
                .headers((headers) -> headers.location(location(user.getId())));
        } catch (PersistenceException e) {
            log.error(e.getMessage(), e);
            return HttpResponse.badRequest();
        }
    }

    @Put("/")
    public HttpResponse<?> update(@NotNull @Valid @Body final UserData data) {
        final Long id = data.getId();

        User tmp = userRepository.findByIdOrThrowException(id);
        User.merge(tmp, data);

        final User user = userRepository.save(tmp);
        return HttpResponse
            .ok(user)
            .headers((headers) -> headers.location(location(id)));
    }

    @Get("/{id}")
    public User get(@NotNull @PathVariable final Long id) {
        return userRepository.findByIdOrThrowException(id);
    }

    @Delete("/{id}")
    public HttpResponse<?> delete(@NotNull final Long id) {
        try {
            final User user = userRepository.findByIdOrThrowException(id);
            if (user.getProfile() == Profile.MASTER) {
                throw ApplicationException
                    .of("It's not allowed to delete a master user.")
                    .badRequest();
            }

            userRepository.delete(user);

            final ResultContent<?> rs = ResultContent.of().message("User (id: %s) successfully deleted.", user.getId());

            return HttpResponse.ok(rs);
        } catch (PersistenceException e) {
            return HttpResponse.badRequest();
        }
    }

    private URI location(final Long id) {
        return URI.create("/users/" + id);
    }

}
