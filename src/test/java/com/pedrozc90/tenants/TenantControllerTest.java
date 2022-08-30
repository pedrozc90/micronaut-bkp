package com.pedrozc90.tenants;

import com.pedrozc90.tenants.models.Tenant;
import com.pedrozc90.tenants.models.TenantRegistration;
import com.pedrozc90.tenants.repo.TenantRepository;
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
public class TenantControllerTest {

    private String accessToken;

    private BlockingHttpClient blockingClient;

    @Inject
    @Client("/")
    private HttpClient client;

    @Inject
    private TenantRepository tenantRepository;

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
    public void testFindExistingUser() {
        final long tenantId = 1;
        final HttpRequest<?> request = HttpRequest.GET(String.format("/tenants/%d", tenantId)).bearerAuth(accessToken);
        final Tenant tenant = blockingClient.retrieve(request, Tenant.class);
        Assertions.assertNotNull(tenant);
        Assertions.assertEquals(tenantId, tenant.getId());
        Assertions.assertNotNull(tenant.getName());
        Assertions.assertNotNull(tenant.getAudit());
    }

    @Test
    public void testFindNonExistingUserReturns404() {
        final HttpClientResponseException e = Assertions.assertThrows(HttpClientResponseException.class, () -> {
            final HttpRequest<?> request = HttpRequest.GET(String.format("/tenants/%d", 1_000)).bearerAuth(accessToken);
            final Tenant tenant = blockingClient.retrieve(request, Tenant.class);
            Assertions.assertNull(tenant);
        });
        Assertions.assertNotNull(e);
        Assertions.assertNotNull(e.getResponse());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
    }

    @Test
    public void testTenantCrudOperations() {
        final List<Long> ids = new ArrayList<>();

        {
            final TenantRegistration cmd = new TenantRegistration("????");
            final HttpRequest<?> request = HttpRequest.POST("/users", cmd).bearerAuth(accessToken);
            final HttpResponse<Tenant> response = blockingClient.exchange(request, Tenant.class);

            Assertions.assertNotNull(response);
            Assertions.assertEquals(HttpStatus.CREATED, response.getStatus());

            final Tenant tenant = response.getBody().orElse(null);
            Assertions.assertNotNull(tenant);

            ids.add(tenant.getId());
        }

        for (final Long id : ids) {
            final HttpRequest<?> request = HttpRequest.DELETE(String.format("/tenants/%d", id)).bearerAuth(accessToken);
            final HttpResponse<?> response = blockingClient.exchange(request);
            Assertions.assertNotNull(response);
            Assertions.assertEquals(HttpStatus.OK, response.getStatus());
        }

        tenantRepository.resetSequence();
    }

}
