package com.pedrozc90.core.authentication;

import com.pedrozc90.core.utils.AuthenticationUtils;
import com.pedrozc90.logs.models.AccessAction;
import com.pedrozc90.logs.models.AccessLog;
import com.pedrozc90.logs.repo.AccessLogRepository;
import com.pedrozc90.users.models.User;
import com.pedrozc90.users.repo.UserRepository;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpHeaders;
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

import java.net.InetSocketAddress;
import java.util.Optional;

@Singleton
@Replaces(AccessRefreshTokenLoginHandler.class)
public class CustomAccessRefreshTokenLoginHandler implements LoginHandler {

    protected final AccessRefreshTokenGenerator accessRefreshTokenGenerator;
    protected final AccessLogRepository accessLogRepository;
    protected final UserRepository userRepository;

    public CustomAccessRefreshTokenLoginHandler(final AccessRefreshTokenGenerator accessRefreshTokenGenerator,
                                                final AccessLogRepository accessLogRepository,
                                                final UserRepository userRepository) {
        this.accessRefreshTokenGenerator = accessRefreshTokenGenerator;
        this.accessLogRepository = accessLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public MutableHttpResponse<?> loginSuccess(final Authentication authentication, final HttpRequest<?> request) {
        final Long userId = AuthenticationUtils.getUserId(authentication);
        final Optional<AccessRefreshToken> accessRefreshTokenOptional = this.accessRefreshTokenGenerator.generate(authentication);
        if (accessRefreshTokenOptional.isPresent()) {
            final AccessRefreshToken accessRefreshToken = accessRefreshTokenOptional.get();
            createAccessLog(AccessAction.LOGIN, accessRefreshToken.getAccessToken(), userId, request);
            return HttpResponse.ok(accessRefreshToken);
        }
        createAccessLog(AccessAction.LOGIN_FAILED, null, userId, request);
        return HttpResponse.serverError();
    }

    @Override
    public MutableHttpResponse<?> loginRefresh(final Authentication authentication, final String refreshToken, final HttpRequest<?> request) {
        final Long userId = AuthenticationUtils.getUserId(authentication);
        final Optional<AccessRefreshToken> accessRefreshTokenOptional = this.accessRefreshTokenGenerator.generate(refreshToken, authentication);
        if (accessRefreshTokenOptional.isPresent()) {
            final AccessRefreshToken accessRefreshToken = accessRefreshTokenOptional.get();
            createAccessLog(AccessAction.LOGIN_REFRESH, accessRefreshToken.getAccessToken(), userId, request);
            return HttpResponse.ok(accessRefreshToken);
        }
        createAccessLog(AccessAction.LOGIN_REFRESH_FAILED, null, userId, request);
        return HttpResponse.serverError();
    }

    @Override
    public MutableHttpResponse<?> loginFailed(final AuthenticationResponse authenticationResponse, final HttpRequest<?> request) {
        createAccessLog(AccessAction.LOGIN_FAILED, null, null, request);
        throw new AuthenticationException(authenticationResponse.getMessage().orElse(null));
    }

    private void createAccessLog(final AccessAction action, final String token, final Long userId, final HttpRequest<?> request) {
        final String userAgent = request.getHeaders().get(HttpHeaders.USER_AGENT);
        final String remoteAddress = Optional.ofNullable(request.getHeaders().get("x-forwarded-for"))
            .orElse(Optional.of(request.getRemoteAddress()).map(InetSocketAddress::toString).orElse(null));

        final Optional<User> userOpt = userRepository.findById(userId);

        final AccessLog accessLog = AccessLog.builder()
            .action(action)
            .address(remoteAddress)
            .userAgent(userAgent)
            .token(token)
            .build();

        userOpt.ifPresent(accessLog::setUser);

        // TODO: no entity manager
        accessLogRepository.save(accessLog);
    }

}
