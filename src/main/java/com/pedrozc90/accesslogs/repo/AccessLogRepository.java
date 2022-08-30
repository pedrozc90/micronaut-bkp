package com.pedrozc90.accesslogs.repo;

import com.pedrozc90.core.data.CrudRepository;
import com.pedrozc90.core.utils.AuthenticationUtils;
import com.pedrozc90.accesslogs.models.AccessAction;
import com.pedrozc90.accesslogs.models.AccessLog;
import com.pedrozc90.accesslogs.models.QAccessLog;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.jwt.render.AccessRefreshToken;
import jakarta.inject.Singleton;

import java.net.InetSocketAddress;
import java.util.Optional;

@Singleton
public class AccessLogRepository extends CrudRepository<AccessLog> {

    public AccessLogRepository() {
        super("access_logs", AccessLog.class, QAccessLog.accessLog);
    }

    public void registerLoginFailed(final HttpRequest<?> request) {
        register(AccessAction.LOGIN_FAILED, request, null, null);
    }

    public void register(final AccessAction action, final HttpRequest<?> request,
                         final Authentication authentication) {
        register(action, request, authentication, null);
    }

    public void register(final AccessAction action, final HttpRequest<?> request,
                         final Authentication authentication,
                         final AccessRefreshToken accessRefreshToken) {

        final String userAgent = request.getHeaders().get(HttpHeaders.USER_AGENT);

        final String remoteAddress = Optional.ofNullable(request.getHeaders().get("x-forwarded-for"))
            .orElse(Optional.of(request.getRemoteAddress()).map(InetSocketAddress::toString).orElse(null));

        final AccessLog log = new AccessLog();
        log.setAction(action);
        log.setUserAgent(userAgent);
        log.setAddress(remoteAddress);
        if (accessRefreshToken != null) {
            log.setToken(accessRefreshToken.getAccessToken());
        }
        if (authentication != null) {
            log.setUsername(authentication.getName());
            log.setUserId(AuthenticationUtils.getUserId(authentication));
        }

        insert(log);
    }

}