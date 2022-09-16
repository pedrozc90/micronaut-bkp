package com.pedrozc90.token.repo;

import com.pedrozc90.core.data.CrudRepository;
import com.pedrozc90.token.models.AccessAction;
import com.pedrozc90.token.models.AccessToken;
import com.pedrozc90.token.models.QAccessToken;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.jwt.render.AccessRefreshToken;
import jakarta.inject.Singleton;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.net.InetSocketAddress;
import java.security.Principal;
import java.util.Optional;

@Singleton
public class AccessTokenRepository extends CrudRepository<AccessToken, Long> {

    public AccessTokenRepository(final EntityManager em) {
        super(em, AccessToken.class, QAccessToken.accessToken1);
    }

    public void register(@NotNull final AccessAction action, @NotNull final HttpRequest<?> request) {
        register(action, null, null, request);
    }

    public void register(@NotNull final AccessAction action, @NotNull final Authentication authentication,
                         @NotNull final HttpRequest<?> request) {
        register(action, authentication, null, request);
    }

    @Transactional
    public void register(@NotNull final AccessAction action, @Nullable final Authentication authentication,
                         @Nullable final AccessRefreshToken accessRefreshToken, @NotNull final HttpRequest<?> request) {
        final String userAgent = Optional.ofNullable(request.getHeaders().get(HttpHeaders.USER_AGENT))
            .map((s) -> StringUtils.substring(s, 0, 255))
            .map(StringUtils::trimToNull)
            .orElse(null);

        final String remoteAddress = Optional.ofNullable(request.getHeaders().get("x-forwarded-for"))
            .or(() -> Optional.of(request.getRemoteAddress()).map(InetSocketAddress::toString))
            .map((s) -> StringUtils.substring(s, 0, 255))
            .map(StringUtils::trimToNull)
            .orElse(null);

        final String username = Optional.ofNullable(authentication).map(Principal::getName).orElse(null);

        final AccessToken at = new AccessToken();
        at.setAction(action);
        at.setUserAgent(userAgent);
        at.setAddress(remoteAddress);
        at.setUsername(username);
        Optional.ofNullable(accessRefreshToken).ifPresent((v) -> {
            at.setAccessToken(v.getAccessToken());
            at.setRefreshToken(v.getRefreshToken());
        });
        save(at);
    }
}