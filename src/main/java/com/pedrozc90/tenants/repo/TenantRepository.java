package com.pedrozc90.tenants.repo;

import com.pedrozc90.core.data.CrudRepository;
import com.pedrozc90.core.exceptions.ApplicationException;
import com.pedrozc90.tenants.models.Tenant;
import com.pedrozc90.tenants.models.TenantData;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Singleton;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Singleton
public class TenantRepository implements CrudRepository<Tenant> {

    private static final Long ALL_TENANTS = -1L;

    private final EntityManager em;

    public TenantRepository(final EntityManager em) {
        this.em = em;
    }

    @Override
    @ReadOnly
    public Optional<Tenant> findById(final Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(em.find(Tenant.class, id));
    }

    @Override
    public Tenant findByIdOrThrowException(long id) throws ApplicationException {
        final Optional<Tenant> opt = findById(id);
        if (opt.isEmpty()) {
            throw ApplicationException.of("Tenant (id: %d) not found.", id).notFound();
        }
        return opt.get();
    }

    @ReadOnly
    public List<Tenant> fetch() {
        final String queryStr = "SELECT t FROM Tenant as t ORDER BY t.id ASC";
        final TypedQuery<Tenant> query = em.createQuery(queryStr, Tenant.class);
        return query.getResultList();
    }

    @Override
    @Transactional
    public Tenant save(final Tenant entity) {
        em.persist(entity);
        return entity;
    }

    @Override
    @Transactional
    public List<Tenant> saveMany(final List<Tenant> entities) throws ApplicationException {
        throw new ApplicationException("Method Not Implemented");
    }

    @Override
    @Transactional
    public void deleteById(final Long id) {
        findById(id).ifPresent(em::remove);
    }

    @Override
    @Transactional
    public void delete(final Tenant entity) {
        em.remove(entity);
    }

    @Transactional
    public Tenant update(final Tenant tenant, final TenantData data) {
        if (tenant == null) return null;
        tenant.setName(data.getName());
        tenant.setAudit(data.getAudit());
        return em.merge(tenant);
    }

    @Override
    public void resetSequence() {
        em.createNativeQuery("CALL reset_table_sequence(:table_name);")
            .setParameter("table_name", "tenants")
            .executeUpdate();
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

}
