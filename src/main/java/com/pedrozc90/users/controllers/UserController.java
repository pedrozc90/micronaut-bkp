package com.pedrozc90.users.controllers;

import com.pedrozc90.core.exceptions.ApplicationException;
import com.pedrozc90.core.models.ResultContent;
import com.pedrozc90.users.models.*;
import com.pedrozc90.users.repo.UserRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
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
    @Transactional
    public List<User> fetch(@QueryValue(value = "page", defaultValue = "1") final long page,
                            @QueryValue(value = "rpp", defaultValue = "15") final long rpp) {
        return userRepository.builder()
            .orderBy(QUser.user.id.asc())
            .select(QUser.user)
            .limit(rpp)
            .offset((page - 1) * rpp)
            .fetch();
    }

    @Post("/")
    public HttpResponse<?> save(@Valid @Body final UserRegistration data) {
        try {
            if (userRepository.validateEmail(data.getEmail())) {
                throw ApplicationException.of("Email %s already in use.", data.getEmail());
            } else if (userRepository.validateUsername(data.getUsername())) {
                throw ApplicationException.of("Username %s already in use.", data.getUsername());
            } else if (!StringUtils.equals(data.getPassword(), data.getPasswordConfirm())) {
                throw ApplicationException.of("Password and password confirm do not match.");
            }

            final User user = userRepository.register(data);

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

    @Patch("/{id}/activate")
    public HttpResponse<?> activate(@NotNull @PathVariable final Long id) {
        final User user = userRepository.findByIdOrThrowException(id);
        user.setActive(true);
        userRepository.save(user);
        return HttpResponse.noContent();
    }

    @Patch("/{id}/deactivate")
    public HttpResponse<?> deactivate(@NotNull @PathVariable final Long id) {
        final User user = userRepository.findByIdOrThrowException(id);
        user.setActive(false);
        userRepository.save(user);
        return HttpResponse.noContent();
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
