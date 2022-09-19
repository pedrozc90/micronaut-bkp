package com.pedrozc90.core.authentication;

import com.pedrozc90.core.exceptions.TokenNotFoundException;
import com.pedrozc90.core.utils.AuthenticationUtils;
import com.pedrozc90.token.models.RefreshToken;
import com.pedrozc90.token.repo.RefreshTokenRepository;
import com.pedrozc90.users.models.Profile;
import com.pedrozc90.users.models.User;
import com.pedrozc90.users.repo.UserRepository;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.errors.OauthErrorResponseException;
import io.micronaut.security.token.event.RefreshTokenGeneratedEvent;
import io.micronaut.security.token.refresh.RefreshTokenPersistence;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.micronaut.security.errors.IssuingAnAccessTokenErrorCode.INVALID_GRANT;

@Singleton
public class RefreshTokenHandler implements RefreshTokenPersistence {

    @Inject
    private RefreshTokenRepository refreshTokenRepository;

    @Inject
    private UserRepository userRepository;

    @Override
    @EventListener
    public void persistToken(final RefreshTokenGeneratedEvent event) {
        final Authentication authentication = event.getAuthentication();
        final String username = authentication.getName();
        final String refreshToken = event.getRefreshToken();
        refreshTokenRepository.register(username, refreshToken);
    }

    @Override
    public Publisher<Authentication> getAuthentication(final String refreshToken) {
        return Flux.create((emitter) -> {
            final Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByRefreshToken(refreshToken);
            if (refreshTokenOpt.isEmpty()) {
                emitter.error(new TokenNotFoundException());
                return;
            }

            final RefreshToken rt = refreshTokenOpt.get();
            if (rt.isRevoked()) {
                emitter.error(new OauthErrorResponseException(INVALID_GRANT, "refresh token revoked", null));
            }

            final Optional<User> userOpt = userRepository.findByUsername(rt.getUsername());

            if (userOpt.isEmpty()) {
                emitter.error(new TokenNotFoundException());
                return;
            }

            final User user = userOpt.get();
            if (user.isNotActive()) {
                emitter.error(AuthenticationResponse.exception(AuthenticationFailureReason.ACCOUNT_LOCKED));
                return;
            }

            final String username = user.getUsername();
            final Profile profile = user.getProfile();

            final Map<String, Object> attributes = new HashMap<>();
            AuthenticationUtils.setUserId(user, attributes);

            emitter.next(Authentication.build(username, profile.getRoles(), attributes));
            emitter.complete();
        }, FluxSink.OverflowStrategy.ERROR);
    }

}
