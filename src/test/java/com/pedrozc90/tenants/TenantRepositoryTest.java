package com.pedrozc90.tenants;

import com.pedrozc90.tenants.models.Tenant;
import com.pedrozc90.tenants.repo.TenantRepository;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

@MicronautTest
public class TenantRepositoryTest {

    @Inject
    private TenantRepository tenantRepository;

    @BeforeAll
    public void setup() {
        tenantRepository.setAllTenantsSession();
    }

    @Test
    public void testFetch() {
        final List<Tenant> tenants = tenantRepository.fetch();
        Assertions.assertNotNull(tenants);
        Assertions.assertEquals(2, tenants.size());
    }

    @Test
    public void testFindById() {
        final long tenantId = 1L;

        final Optional<Tenant> tenantOptional = tenantRepository.findById(tenantId);
        Assertions.assertNotNull(tenantOptional);
        Assertions.assertTrue(tenantOptional.isPresent());
        Assertions.assertNotNull(tenantOptional.get());

        final Tenant tenant = tenantOptional.get();
        Assertions.assertEquals(tenantId, tenant.getId());
        Assertions.assertNotNull(tenant.getName());
        Assertions.assertNotNull(tenant.getAudit());
    }

}
