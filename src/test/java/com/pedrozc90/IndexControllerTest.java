package com.pedrozc90;

import com.pedrozc90.core.models.Ping;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@MicronautTest
public class IndexControllerTest {

    private BlockingHttpClient blockingClient;

    @Inject
    @Client("/")
    private HttpClient client;

    @BeforeEach
    public void setup() {
        blockingClient = client.toBlocking();
    }

    @Test
    @DisplayName("index")
    public void testIndex() {
        final HttpRequest<String> request = HttpRequest.GET("/");
        final String body = blockingClient.retrieve(request);
        Assertions.assertNotNull(body);
        Assertions.assertEquals("sanity check", body);
    }

    @Test
    @DisplayName("ping")
    public void testPing() {
        final HttpRequest<Ping> request = HttpRequest.GET("/ping");
        final Ping body = blockingClient.retrieve(request, Ping.class);
        Assertions.assertNotNull(body);
        Assertions.assertNotNull(body.getTimestamp());
    }

}
