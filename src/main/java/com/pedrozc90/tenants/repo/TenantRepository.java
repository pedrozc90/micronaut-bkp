package com.pedrozc90.tenants.repo;

import com.pedrozc90.core.data.CrudRepository;
import com.pedrozc90.core.models.Page;
import com.pedrozc90.core.querydsl.JPAQuery;
import com.pedrozc90.tenants.models.QTenant;
import com.pedrozc90.tenants.models.Tenant;
import com.pedrozc90.tenants.models.TenantData;
import com.pedrozc90.tenants.models.TenantRegistration;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Singleton;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Optional;

@Singleton
public class TenantRepository extends CrudRepository<Tenant, Long> {

    private static final Long ALL_TENANTS = -1L;

    public TenantRepository(final EntityManager em) {
        super(em, Tenant.class, QTenant.tenant);
    }

    @ReadOnly
    public Page<Tenant> fetch(final int page, final int rpp, final String q) {
        final JPAQuery<Tenant> query = createQuery().from(QTenant.tenant);

        if (StringUtils.isNotBlank(q)) {
            query.where(QTenant.tenant.name.containsIgnoreCase(q));
        }

        query.orderBy(QTenant.tenant.name.asc());

        return Page.create(query, page, rpp);
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
    public Tenant update(final Tenant tenant, final TenantData data) {
        Optional.ofNullable(data.getName()).ifPresent(tenant::setName);
        Optional.ofNullable(data.getAudit()).ifPresent(tenant::setAudit);
        return update(tenant);
    }

    @Transactional
    public Tenant register(final TenantRegistration data) {
        final Tenant tenant = new Tenant();
        tenant.setName(data.getName());
        return save(tenant);
    }

}
