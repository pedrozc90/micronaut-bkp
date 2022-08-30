package com.pedrozc90.tenants.repo;

import com.pedrozc90.core.data.CrudRepository;
import com.pedrozc90.tenants.models.QTenant;
import com.pedrozc90.tenants.models.Tenant;
import com.pedrozc90.tenants.models.TenantRegistration;
import jakarta.inject.Singleton;

import javax.transaction.Transactional;

@Singleton
public class TenantRepository extends CrudRepository<Tenant> {

    private static final Long ALL_TENANTS = -1L;

    public TenantRepository() {
        super("tenants", Tenant.class, QTenant.tenant);
    }

    // CUSTOM QUERIES
    @Transactional
    public void setTenantSession(final Long tenantId) {
        if (tenantId == null) return;
        em.createNamedQuery("set_tenant")
            .setParameter("tenant_id", tenantId)
            .getSingleResult();
    }

    public void setAllTenantsSession() {
        this.setTenantSession(ALL_TENANTS);
    }

    @Transactional
    public void resetTenantSession() {
        this.em.createNamedQuery("reset_tenant").executeUpdate();
    }

    @Transactional
    public Tenant register(final TenantRegistration data) {
        final Tenant tenant = new Tenant();
        tenant.setName(data.getName());
        return insert(tenant);
    }

}
