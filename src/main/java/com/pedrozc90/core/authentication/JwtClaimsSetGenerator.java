package com.pedrozc90.core.authentication;

import com.nimbusds.jwt.JWTClaimsSet;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.runtime.ApplicationConfiguration;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.config.TokenConfiguration;
import io.micronaut.security.token.jwt.generator.claims.ClaimsAudienceProvider;
import io.micronaut.security.token.jwt.generator.claims.ClaimsGenerator;
import io.micronaut.security.token.jwt.generator.claims.JWTClaimsSetGenerator;
import io.micronaut.security.token.jwt.generator.claims.JwtIdGenerator;
import jakarta.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Singleton
@Replaces(JWTClaimsSetGenerator.class)
public class JwtClaimsSetGenerator implements ClaimsGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(JwtClaimsSetGenerator.class);
    private static final String ROLES_KEY = "rolesKey";
    private final TokenConfiguration tokenConfiguration;
    private final JwtIdGenerator jwtIdGenerator;
    private final ClaimsAudienceProvider claimsAudienceProvider;
    private final String appName;

    public JwtClaimsSetGenerator(final TokenConfiguration tokenConfiguration,
                                 @Nullable final JwtIdGenerator jwtIdGenerator,
                                 @Nullable final ClaimsAudienceProvider claimsAudienceProvider,
                                 @Nullable final ApplicationConfiguration applicationConfiguration) {
        this.tokenConfiguration = tokenConfiguration;
        this.jwtIdGenerator = jwtIdGenerator;
        this.claimsAudienceProvider = claimsAudienceProvider;
        this.appName = applicationConfiguration != null ? (String) applicationConfiguration.getName().orElse("micronaut") : "micronaut";
    }

    public Map<String, Object> generateClaims(final Authentication authentication, @Nullable final Integer expiration) {
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        this.populateIat(builder);
        this.populateExp(builder, expiration);
        this.populateJti(builder);
        this.populateIss(builder);
        this.populateAud(builder);
        this.populateNbf(builder);
        this.populateWithAuthentication(builder, authentication);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Generated claim set: {}", builder.build().toJSONObject());
        }
        return builder.build().getClaims();
    }

    protected void populateIss(final JWTClaimsSet.Builder builder) {
        if (this.appName != null) {
            builder.issuer(this.appName);
        }
    }

    protected void populateSub(final JWTClaimsSet.Builder builder, final Authentication authentication) {
        builder.subject(authentication.getName());
    }

    protected void populateAud(final JWTClaimsSet.Builder builder) {
        if (this.claimsAudienceProvider != null) {
            builder.audience(this.claimsAudienceProvider.audience());
        }

    }

    protected void populateExp(final JWTClaimsSet.Builder builder, @Nullable final Integer expiration) {
        if (expiration != null) {
            LOG.debug("Setting expiration to {}", expiration);
            builder.expirationTime(Date.from(Instant.now().plus((long) expiration, ChronoUnit.SECONDS)));
        }
    }

    protected void populateNbf(final JWTClaimsSet.Builder builder) {
        builder.notBeforeTime(new Date());
    }

    protected void populateIat(final JWTClaimsSet.Builder builder) {
        builder.issueTime(new Date());
    }

    protected void populateJti(final JWTClaimsSet.Builder builder) {
        if (this.jwtIdGenerator != null) {
            builder.jwtID(this.jwtIdGenerator.generateJtiClaim());
        }
    }

    protected void populateWithAuthentication(final JWTClaimsSet.Builder builder, final Authentication authentication) {
        this.populateSub(builder, authentication);
        final Map<String, Object> attributes = authentication.getAttributes();
        Objects.requireNonNull(builder);
        attributes.forEach(builder::claim);

        String rolesKey = this.tokenConfiguration.getRolesName();
        if (!StringUtils.equalsIgnoreCase(rolesKey, "roles")) {
            builder.claim(ROLES_KEY, rolesKey);
        }
        builder.claim(rolesKey, authentication.getRoles());
    }

    public Map<String, Object> generateClaimsSet(final Map<String, ?> oldClaims, final Integer expiration) {
        final JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        final List<String> excludedClaims = Arrays.asList("exp", "iat", "nbf");

        oldClaims.entrySet().stream()
            .filter((e) -> !excludedClaims.contains(e.getKey()))
            .forEach((e) -> builder.claim(e.getKey(), e.getValue()));

        this.populateExp(builder, expiration);
        this.populateIat(builder);
        this.populateNbf(builder);
        return builder.build().getClaims();
    }

}
