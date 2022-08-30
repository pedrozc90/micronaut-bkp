package com.pedrozc90.accesslogs;

import com.pedrozc90.accesslogs.models.AccessAction;
import com.pedrozc90.accesslogs.models.AccessLog;
import com.pedrozc90.accesslogs.repo.AccessLogRepository;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

@MicronautTest
public class AccessLogRepositoryTest {

    @Inject
    private AccessLogRepository repo;

    @AfterEach
    public void reset() {
        try {
            final List<AccessLog> list = repo.fetchAll();
            list.forEach((v) -> repo.delete(v));
        } finally {
            repo.resetSequence();
        }
    }

    @Test
    public void insert() {
        final AccessLog log = new AccessLog();
        log.setAction(AccessAction.LOGIN);
        log.setUserAgent("USER_AGENT");
        log.setAddress("http:127.0.0.1:9000");
        log.setToken("0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x0x");
        log.setUsername("unknown");
        log.setUserId(1L);

        final AccessLog log_2 = repo.insert(log);
        Assertions.assertNotNull(log_2);
        Assertions.assertNotNull(log_2.getAudit());
        Assertions.assertNotNull(log_2.getAction());
        Assertions.assertNotNull(log_2.getAddress());
        Assertions.assertNotNull(log_2.getUserAgent());
        Assertions.assertNotNull(log_2.getUsername());
        Assertions.assertNotNull(log_2.getUserId());
    }

}
