package com.pedrozc90.auth;

import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import com.pedrozc90.core.tests.ApplicationClient;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

@MicronautTest
public class ApplicationClientTest {

    @Inject
    private ApplicationClient client;

    @Test
    public void verifyJwtAuthenticationWorksWithDeclarativeClient() throws ParseException {
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("master", "1");
        final BearerAccessRefreshToken bearer = client.login(credentials);

        Assertions.assertNotNull(bearer);
        Assertions.assertNotNull(bearer.getAccessToken());
        Assertions.assertTrue(JWTParser.parse(bearer.getAccessToken()) instanceof SignedJWT);

        final String msg = client.secured("Bearer " + bearer.getAccessToken());
        Assertions.assertEquals(credentials.getIdentity(), msg);
    }

}
