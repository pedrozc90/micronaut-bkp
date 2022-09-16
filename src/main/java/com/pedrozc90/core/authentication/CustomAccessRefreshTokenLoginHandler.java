package com.pedrozc90.core.authentication;

import com.pedrozc90.token.models.AccessAction;
import com.pedrozc90.token.repo.AccessTokenRepository;
import com.pedrozc90.users.repo.UserRepository;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.AuthenticationException;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.handlers.LoginHandler;
import io.micronaut.security.token.jwt.bearer.AccessRefreshTokenLoginHandler;
import io.micronaut.security.token.jwt.generator.AccessRefreshTokenGenerator;
import io.micronaut.security.token.jwt.render.AccessRefreshToken;
import jakarta.inject.Singleton;

import java.util.Optional;

@Singleton
@Replaces(AccessRefreshTokenLoginHandler.class)
public class CustomAccessRefreshTokenLoginHandler implements LoginHandler {

    protected final AccessRefreshTokenGenerator accessRefreshTokenGenerator;
    protected final AccessTokenRepository accessTokenRepository;
    protected final UserRepository userRepository;

    public CustomAccessRefreshTokenLoginHandler(final AccessRefreshTokenGenerator accessRefreshTokenGenerator,
                                                final AccessTokenRepository accessTokenRepository,
                                                final UserRepository userRepository) {
        this.accessRefreshTokenGenerator = accessRefreshTokenGenerator;
        this.accessTokenRepository = accessTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public MutableHttpResponse<?> loginSuccess(final Authentication authentication, final HttpRequest<?> request) {
        final Optional<AccessRefreshToken> accessRefreshTokenOpt = this.accessRefreshTokenGenerator.generate(authentication);
        if (accessRefreshTokenOpt.isPresent()) {
            final AccessRefreshToken accessRefreshToken = accessRefreshTokenOpt.get();
            accessTokenRepository.register(AccessAction.LOGIN, authentication, accessRefreshToken, request);
            return HttpResponse.ok(accessRefreshToken);
        }
        accessTokenRepository.register(AccessAction.LOGIN_FAILED, authentication, request);
        return HttpResponse.serverError();
    }

    @Override
    public MutableHttpResponse<?> loginRefresh(final Authentication authentication, final String refreshToken, final HttpRequest<?> request) {
        final Optional<AccessRefreshToken> accessRefreshTokenOpt = this.accessRefreshTokenGenerator.generate(refreshToken, authentication);
        if (accessRefreshTokenOpt.isPresent()) {
            final AccessRefreshToken accessRefreshToken = accessRefreshTokenOpt.get();
            accessTokenRepository.register(AccessAction.LOGIN_REFRESH, authentication, accessRefreshToken, request);
            return HttpResponse.ok(accessRefreshToken);
        }
        accessTokenRepository.register(AccessAction.LOGIN_REFRESH_FAILED, authentication, request);
        return HttpResponse.serverError();
    }

    @Override
    public MutableHttpResponse<?> loginFailed(final AuthenticationResponse authenticationResponse, final HttpRequest<?> request) {
        accessTokenRepository.register(AccessAction.LOGIN_FAILED, request);
        throw new AuthenticationException(authenticationResponse.getMessage().orElse(null));
    }

}
