package com.pedrozc90.tenants;

import com.pedrozc90.tenants.models.QTenant;
import com.pedrozc90.tenants.models.Tenant;
import com.pedrozc90.tenants.repo.TenantRepository;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

@MicronautTest
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class TenantRepositoryTest {

    @Inject
    private TenantRepository repo;

    // @BeforeEach
    // public void setup() {
    //     repo.setAllTenantsSession();
    // }

    @Test
    @Order(1)
    public void testFetch() {
        final List<Tenant> tenants = repo.fetchAll();
        Assertions.assertNotNull(tenants);
        Assertions.assertTrue(tenants.size() >= 2);
    }

    @Test
    @Order(2)
    public void testFindById() {
        final long tenantId = 1L;

        final Optional<Tenant> tenantOpt = repo.findById(tenantId);
        Assertions.assertNotNull(tenantOpt);
        Assertions.assertTrue(tenantOpt.isPresent());
        Assertions.assertNotNull(tenantOpt.get());

        final Tenant t = tenantOpt.get();
        Assertions.assertEquals(tenantId, t.getId());
        Assertions.assertNotNull(t.getName());
        Assertions.assertNotNull(t.getAudit());
    }

    @Test
    @Order(3)
    public void testInsert() {
        final Tenant t = Optional.of(Tenant.builder().name("Unknown").build())
            .map((v) -> repo.save(v))
            .orElse(null);

        Assertions.assertNotNull(t);
        Assertions.assertNotNull(t.getId());
        Assertions.assertNotNull(t.getName());
    }

    @Test
    @Order(4)
    public void testDelete() {
        repo.findOne(QTenant.tenant.name.equalsIgnoreCase("Unknown"))
            .ifPresent(v -> {
                repo.delete(v);
                repo.commit();
            });
        repo.resetSequence();
    }

}
