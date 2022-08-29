package com.pedrozc90.logs.repo;

import com.pedrozc90.core.data.CrudRepository;
import com.pedrozc90.core.exceptions.ApplicationException;
import com.pedrozc90.logs.models.AccessLog;
import com.pedrozc90.users.models.User;
import com.pedrozc90.users.models.UserData;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Singleton;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Singleton
public class AccessLogRepository implements CrudRepository<AccessLog> {

    private final EntityManager em;

    public AccessLogRepository(final EntityManager em) {
        this.em = em;
    }

    @Override
    @ReadOnly
    public Optional<AccessLog> findById(final Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(em.find(AccessLog.class, id));
    }

    @Override
    public AccessLog findByIdOrThrowException(long id) throws ApplicationException {
        final Optional<AccessLog> opt = findById(id);
        if (opt.isEmpty()) {
            // throw ApplicationException.of("User (id: %d) not found.", id).notFound();
            throw new ApplicationException();
        }
        return opt.get();
    }

    @ReadOnly
    public List<AccessLog> fetch() {
        final String queryStr = "SELECT al FROM AccessLog as al ORDER BY al.id ASC";
        final TypedQuery<AccessLog> query = em.createQuery(queryStr, AccessLog.class);
        return query.getResultList();
    }

    @Override
    @Transactional
    public AccessLog save(final AccessLog entity) {
        em.persist(entity);
        return entity;
    }

    @Override
    @Transactional
    public List<AccessLog> saveMany(final List<AccessLog> entities) throws ApplicationException {
        throw new ApplicationException("Method Not Implemented");
    }

    @Override
    @Transactional
    public void deleteById(final Long id) {
        findById(id).ifPresent(em::remove);
    }

    @Override
    @Transactional
    public void delete(final AccessLog entity) {
        em.remove(entity);
    }

    @Transactional
    public User update(final AccessLog user, final UserData data) {
        return null;
    }

    @Transactional
    public void resetSequence() {
         em.createNativeQuery("CALL reset_table_sequence(:table_name);")
             .setParameter("table_name", "access_logs")
             .executeUpdate();
    }

}