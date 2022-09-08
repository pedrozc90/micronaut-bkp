package com.pedrozc90.core.authentication;

import com.pedrozc90.users.models.User;
import com.pedrozc90.users.repo.UserRepository;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.security.handlers.LogoutHandler;
import jakarta.inject.Singleton;

import java.util.Optional;

@Singleton
public class CustomLogoutHandler implements LogoutHandler {

    private final UserRepository userRepository;

    public CustomLogoutHandler(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public MutableHttpResponse<?> logout(final HttpRequest<?> request) {
        final Optional<User> userOpt = request.getUserPrincipal().map((v) -> userRepository.findByUsername(v.getName())).orElseGet(null);
        if (userOpt.isPresent()) {
            // create log
        }
        return HttpResponse.noContent();
    }

}
