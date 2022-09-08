package com.pedrozc90.logs.repo;

import com.pedrozc90.core.data.EntityRepository;
import com.pedrozc90.logs.models.AccessAction;
import com.pedrozc90.logs.models.AccessToken;
import com.pedrozc90.logs.models.QAccessToken;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.jwt.render.AccessRefreshToken;
import jakarta.inject.Singleton;

import java.net.InetSocketAddress;
import java.util.Optional;

@Singleton
public class AccessLogRepository extends EntityRepository<AccessToken, Long> {

    public AccessLogRepository() {
        super("access_logs", AccessToken.class, QAccessToken.accessToken1);
    }

    public void registerLoginFailed(final HttpRequest<?> request) {
        register(AccessAction.LOGIN_FAILED, request, null, null);
    }

    public void register(final AccessAction action, final String userAgent,
                         final String address, final String username,
                         final String accessToken, final String refreshToken) {
        final AccessToken at = new AccessToken();
        at.setAction(action);
        at.setUserAgent(userAgent);
        at.setAddress(address);
        at.setUsername(username);
        at.setAccessToken(accessToken);
        at.setRefreshToken(refreshToken);
        save(at);
    }

    public void register(final AccessAction action,
                         final HttpRequest<?> request,
                         final Authentication authentication) {
        register(action, request, authentication, null);
    }

    public void register(final AccessAction action, final HttpRequest<?> request,
                         final Authentication authentication,
                         final AccessRefreshToken accessRefreshToken) {

        final String userAgent = request.getHeaders().get(HttpHeaders.USER_AGENT);

        final String remoteAddress = Optional.ofNullable(request.getHeaders().get("x-forwarded-for"))
            .orElse(Optional.of(request.getRemoteAddress()).map(InetSocketAddress::toString).orElse(null));

        final String username = authentication.getName();

        final String accessToken = accessRefreshToken.getAccessToken();
        final String refreshToken = accessRefreshToken.getRefreshToken();

        register(action, userAgent, remoteAddress, username, accessToken, refreshToken);
    }

}