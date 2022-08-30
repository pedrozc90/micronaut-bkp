package com.pedrozc90.core.authentication;

import com.pedrozc90.accesslogs.models.AccessAction;
import com.pedrozc90.accesslogs.repo.AccessLogRepository;
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
    protected final AccessLogRepository accessLogRepository;

    public CustomAccessRefreshTokenLoginHandler(final AccessRefreshTokenGenerator accessRefreshTokenGenerator,
                                                final AccessLogRepository accessLogRepository) {
        this.accessRefreshTokenGenerator = accessRefreshTokenGenerator;
        this.accessLogRepository = accessLogRepository;
    }

    @Override
    public MutableHttpResponse<?> loginSuccess(final Authentication authentication, final HttpRequest<?> request) {
        final Optional<AccessRefreshToken> accessRefreshTokenOpt = this.accessRefreshTokenGenerator.generate(authentication);
        if (accessRefreshTokenOpt.isPresent()) {
            final AccessRefreshToken accessRefreshToken = accessRefreshTokenOpt.get();
            accessLogRepository.register(AccessAction.LOGIN, request, authentication, accessRefreshToken);
            return HttpResponse.ok(accessRefreshToken);
        }
        accessLogRepository.register(AccessAction.LOGIN_FAILED, request, authentication);
        return HttpResponse.serverError();
    }

    @Override
    public MutableHttpResponse<?> loginRefresh(final Authentication authentication, final String refreshToken, final HttpRequest<?> request) {
        final Optional<AccessRefreshToken> accessRefreshTokenOpt = this.accessRefreshTokenGenerator.generate(refreshToken, authentication);
        if (accessRefreshTokenOpt.isPresent()) {
            final AccessRefreshToken accessRefreshToken = accessRefreshTokenOpt.get();
            accessLogRepository.register(AccessAction.LOGIN_REFRESH, request, authentication, accessRefreshToken);
            return HttpResponse.ok(accessRefreshToken);
        }
        accessLogRepository.register(AccessAction.LOGIN_REFRESH_FAILED, request, authentication);
        return HttpResponse.serverError();
    }

    @Override
    public MutableHttpResponse<?> loginFailed(final AuthenticationResponse authenticationResponse, final HttpRequest<?> request) {
        accessLogRepository.registerLoginFailed(request);
        throw new AuthenticationException(authenticationResponse.getMessage().orElse(null));
    }

}
