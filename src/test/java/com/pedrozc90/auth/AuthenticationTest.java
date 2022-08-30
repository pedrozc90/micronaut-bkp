package com.pedrozc90.auth;

import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import com.pedrozc90.users.models.User;
import com.pedrozc90.users.repo.UserRepository;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.*;

import java.text.ParseException;

@MicronautTest
public class AuthenticationTest {

    private static User user;
    private static final String username = "temporary";
    private static final String password = "temporary";

    private BlockingHttpClient blockingClient;

    @Inject
    @Client("/")
    private HttpClient client;

    @Inject
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        blockingClient = client.toBlocking();

        user = new User();
        user.setEmail(String.format("%s@email.com", username));
        user.setUsername(username);
        user.setPassword(DigestUtils.md5Hex(password));
        user.setActive(true);
        user = userRepository.insert(user);
    }

    @AfterEach
    public void reset() {
        try {
            userRepository.delete(user);
        } finally {
            userRepository.resetSequence();
        }
    }

    @Test
    @SneakyThrows(value = ParseException.class)
    @DisplayName("Should return a valid access_token")
    public void verifyUserAuthentication() {
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);

        final HttpRequest<?> request = HttpRequest.POST("/login", credentials);
        final HttpResponse<BearerAccessRefreshToken> response = blockingClient.exchange(request, BearerAccessRefreshToken.class);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatus());

        final BearerAccessRefreshToken bearer = response.body();
        Assertions.assertNotNull(bearer);
        Assertions.assertNotNull(bearer.getAccessToken());
        Assertions.assertTrue(JWTParser.parse(bearer.getAccessToken()) instanceof SignedJWT);
        Assertions.assertEquals(username, bearer.getUsername());
    }

    @Test
    @DisplayName("Unknown user credentials")
    public void verifyDeactivatedUserAuthentication() {
        user.setActive(false);
        userRepository.save(user);

        final HttpClientResponseException e = Assertions.assertThrows(HttpClientResponseException.class, () -> {
            final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);

            final HttpRequest<?> request = HttpRequest.POST("/login", credentials);
            final HttpResponse<BearerAccessRefreshToken> response = blockingClient.exchange(request, BearerAccessRefreshToken.class);
            Assertions.assertNotNull(response);
        });

        Assertions.assertNotNull(e);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
        Assertions.assertEquals("Account Locked", e.getMessage());
    }

    @Test
    @DisplayName("Unknown user credentials")
    public void verifyNonExistentUserAuthentication() {
        final HttpClientResponseException e = Assertions.assertThrows(HttpClientResponseException.class, () -> {
            final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("unknown", "123456");

            final HttpRequest<?> request = HttpRequest.POST("/login", credentials);
            final HttpResponse<BearerAccessRefreshToken> response = blockingClient.exchange(request, BearerAccessRefreshToken.class);
            Assertions.assertNotNull(response);
        });

        Assertions.assertNotNull(e);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
        Assertions.assertEquals("User Not Found", e.getMessage());
    }

}
