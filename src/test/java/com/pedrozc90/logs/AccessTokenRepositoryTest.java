package com.pedrozc90.logs;

import com.pedrozc90.logs.models.AccessAction;
import com.pedrozc90.logs.models.AccessToken;
import com.pedrozc90.logs.repo.AccessLogRepository;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

@MicronautTest
public class AccessTokenRepositoryTest {

    @Inject
    private AccessLogRepository repo;

    @AfterEach
    public void reset() {
        try {
            final List<AccessToken> list = repo.fetchAll();
            list.forEach((v) -> repo.delete(v));
        } finally {
            repo.resetSequence();
        }
    }

    @Test
    public void insert() {
        final AccessToken log = new AccessToken();
        log.setAction(AccessAction.LOGIN);
        log.setUserAgent("USER_AGENT");
        log.setAddress("http:127.0.0.1:9000");
        log.setToken("0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x");
        log.setUsername("unknown");
        log.setUserId(1L);

        final AccessToken log_2 = repo.insert(log);
        Assertions.assertNotNull(log_2);
        Assertions.assertNotNull(log_2.getAudit());
        Assertions.assertNotNull(log_2.getAction());
        Assertions.assertNotNull(log_2.getAddress());
        Assertions.assertNotNull(log_2.getUserAgent());
        Assertions.assertNotNull(log_2.getUsername());
        Assertions.assertNotNull(log_2.getUserId());
    }

}
