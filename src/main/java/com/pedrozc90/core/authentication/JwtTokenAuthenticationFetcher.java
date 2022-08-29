package com.pedrozc90.core.authentication;

import com.pedrozc90.core.events.AuthenticationValidatedEvent;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.event.TokenValidatedEvent;
import io.micronaut.security.filters.AuthenticationFetcher;
import io.micronaut.security.filters.SecurityFilter;
import io.micronaut.security.token.TokenAuthenticationFetcher;
import io.micronaut.security.token.reader.TokenResolver;
import io.micronaut.security.token.validator.TokenValidator;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Singleton
@Replaces(TokenAuthenticationFetcher.class)
public class JwtTokenAuthenticationFetcher implements AuthenticationFetcher {

    public static final Integer ORDER = 0;
    protected final Collection<TokenValidator> tokenValidators;
    protected final ApplicationEventPublisher<Object> eventPublisher;
    private final TokenResolver tokenResolver;

    public JwtTokenAuthenticationFetcher(final Collection<TokenValidator> tokenValidators,
                                         final ApplicationEventPublisher<Object> eventPublisher,
                                         final TokenResolver tokenResolver) {
        this.tokenValidators = tokenValidators;
        this.eventPublisher = eventPublisher;
        this.tokenResolver = tokenResolver;
    }

    @Override
    public Publisher<Authentication> fetchAuthentication(final HttpRequest<?> request) {
        final Optional<String> tokenOpt = this.tokenResolver.resolveToken(request);
        if (tokenOpt.isEmpty()) {
            return Flux.empty();
        } else {
            final String token = tokenOpt.get();
            return Flux.fromIterable(this.tokenValidators)
                .flatMap((tokenValidator) -> tokenValidator.validateToken(token, request))
                .next()
                .map((authentication) -> {
                    request.setAttribute(SecurityFilter.TOKEN, token);
                    this.eventPublisher.publishEvent(new TokenValidatedEvent(token));
                    this.eventPublisher.publishEvent(new AuthenticationValidatedEvent(authentication));
                    return authentication;
                });
        }
    }

    public int getOrder() {
        return ORDER;
    }

}
