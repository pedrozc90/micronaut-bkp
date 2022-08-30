package com.pedrozc90.tenants;

import com.pedrozc90.tenants.models.QTenant;
import com.pedrozc90.tenants.models.Tenant;
import com.pedrozc90.tenants.repo.TenantRepository;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@MicronautTest
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

        final Optional<Tenant> tenantOptional = repo.findById(tenantId);
        Assertions.assertNotNull(tenantOptional);
        Assertions.assertTrue(tenantOptional.isPresent());
        Assertions.assertNotNull(tenantOptional.get());

        final Tenant tenant = tenantOptional.get();
        Assertions.assertEquals(tenantId, tenant.getId());
        Assertions.assertNotNull(tenant.getName());
        Assertions.assertNotNull(tenant.getAudit());
    }

    @Test
    @Order(3)
    public void testInsert() {
        final Tenant t = new Tenant();
        t.setName("Unknown");
        final Tenant tenant = repo.save(t);

        Assertions.assertNotNull(tenant);
        Assertions.assertNotNull(tenant.getId());
        Assertions.assertNotNull(tenant.getName());
    }

    @Test
    @Order(4)
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public void testDelete() {
        repo.findOne(QTenant.tenant.name.equalsIgnoreCase("Unknown"))
            .ifPresent(v -> repo.delete(v));
        repo.flush();
    }

}
