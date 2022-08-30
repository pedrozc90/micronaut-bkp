package com.pedrozc90.users;

import com.pedrozc90.users.models.User;
import com.pedrozc90.users.models.UserData;
import com.pedrozc90.users.models.UserRegistration;
import com.pedrozc90.users.repo.UserRepository;
import io.micronaut.core.type.Argument;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

@MicronautTest
public class UserControllerTest {

    private String accessToken;

    private BlockingHttpClient blockingClient;

    @Inject
    @Client("/")
    private HttpClient client;

    @Inject
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        blockingClient = client.toBlocking();

        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("master", "1");

        final HttpRequest<?> request = HttpRequest.POST("/login", credentials);
        final HttpResponse<BearerAccessRefreshToken> response = blockingClient.exchange(request, BearerAccessRefreshToken.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatus());

        final BearerAccessRefreshToken bearer = response.body();
        Assertions.assertNotNull(bearer);
        Assertions.assertNotNull(bearer.getAccessToken());
        accessToken = bearer.getAccessToken();
    }

    @Test
    public void supplyAnInvalidOrderTriggersValidationFailure() {
        final HttpClientResponseException e = Assertions.assertThrows(HttpClientResponseException.class, () -> {
            final HttpRequest<?> request = HttpRequest.GET("/users/list?order=foo")
                .bearerAuth(accessToken);
            blockingClient.exchange(request);
        });
        Assertions.assertNotNull(e);
        Assertions.assertNotNull(e.getResponse());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
    }

    @Test
    public void testFindExistingUser() {
        final Long userId = 1L;
        final HttpRequest<?> request = HttpRequest.GET(String.format("/users/%d", userId)).bearerAuth(accessToken);
        final User user = blockingClient.retrieve(request, User.class);
        Assertions.assertNotNull(user);
        Assertions.assertEquals(userId, user.getId());
        Assertions.assertNotNull(user.getEmail());
        Assertions.assertNotNull(user.getUsername());
        Assertions.assertNotNull(user.getProfile());
        Assertions.assertNotNull(user.getAudit());
        Assertions.assertNull(user.getPassword());
    }

    @Test
    public void testFindNonExistingUserReturns404() {
        final HttpClientResponseException e = Assertions.assertThrows(HttpClientResponseException.class, () -> {
            final HttpRequest<?> request = HttpRequest.GET(String.format("/users/%d", 1_000)).bearerAuth(accessToken);
            final User user = blockingClient.retrieve(request, User.class);
            Assertions.assertNull(user);
        });
        Assertions.assertNotNull(e);
        Assertions.assertNotNull(e.getResponse());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
    }

    @Test
    public void testUserCrudOperations() {
        final List<Long> ids = new ArrayList<>();

        {
            final UserRegistration cmd = new UserRegistration("john@email.com", "john", "1", "1");
            final HttpRequest<?> request = HttpRequest.POST("/users", cmd).bearerAuth(accessToken);
            final HttpResponse<User> response = blockingClient.exchange(request, User.class);

            Assertions.assertNotNull(response);
            Assertions.assertEquals(HttpStatus.CREATED, response.getStatus());

            final User user = response.getBody().orElse(null);
            Assertions.assertNotNull(user);

            ids.add(user.getId());
        }

        {
            final UserRegistration cmd = new UserRegistration("mary@email.com", "mary", "1", "1");
            final HttpRequest<?> request = HttpRequest.POST("/users", cmd).bearerAuth(accessToken);
            final HttpResponse<User> response = blockingClient.exchange(request, User.class);

            Assertions.assertNotNull(response);
            Assertions.assertEquals(HttpStatus.CREATED, response.getStatus());

            final User user = response.getBody().orElse(null);
            Assertions.assertNotNull(user);

            ids.add(user.getId());
        }

        {
            // load
            final Long id = ids.get(0);

            final HttpRequest<?> request = HttpRequest.GET("/users/" + id).bearerAuth(accessToken);
            final User user = blockingClient.retrieve(request, User.class);

            Assertions.assertNotNull(user);
            Assertions.assertEquals("john", user.getUsername());
            Assertions.assertEquals("john@email.com", user.getEmail());
            Assertions.assertNotNull(user.getAudit());
            Assertions.assertEquals(1, user.getAudit().getVersion());

            // update
            final UserData cmd = new UserData(id, user.getAudit(), "john@email.com", user.getProfile(), "john2", false);

            final HttpRequest<?> request2 = HttpRequest.PUT("/users", cmd).bearerAuth(accessToken);
            final HttpResponse<User> response2 = blockingClient.exchange(request2, User.class);

            Assertions.assertNotNull(response2);
            Assertions.assertEquals(HttpStatus.OK, response2.getStatus());
        }

        {
            // load
            final Long id = ids.get(0);

            final HttpRequest<?> request = HttpRequest.GET("/users/" + id).bearerAuth(accessToken);
            final User user = blockingClient.retrieve(request, User.class);

            Assertions.assertNotNull(user);
            Assertions.assertEquals("josh2", user.getUsername());
            Assertions.assertEquals("josh@email.com", user.getEmail());
            Assertions.assertNotNull(user.getAudit());
            Assertions.assertEquals(2, user.getAudit().getVersion());
        }

        {
            final HttpRequest<?> request = HttpRequest.GET("/users").bearerAuth(accessToken);
            final List<User> users = blockingClient.retrieve(request, Argument.of(List.class, User.class));

            Assertions.assertEquals(3, users.size());
        }

        for (final Long id : ids) {
            final HttpRequest<?> request = HttpRequest.DELETE("/users/" + id).bearerAuth(accessToken);
            final HttpResponse<?> response = blockingClient.exchange(request);
            Assertions.assertNotNull(response);
            Assertions.assertEquals(HttpStatus.OK, response.getStatus());
        }

        userRepository.resetSequence();
    }

}
