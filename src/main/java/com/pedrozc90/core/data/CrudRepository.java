package com.pedrozc90.core.data;

import com.pedrozc90.core.exceptions.ApplicationException;
import com.pedrozc90.core.querydsl.JPAQuery;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import io.micronaut.core.annotation.NonNull;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class CrudRepository<E> {

    @PersistenceContext
    private EntityManager em;

    private final String tableName;
    private final Class<E> clazz;
    private final EntityPath<E> entityPath;

    public CrudRepository(final String tableName, final Class<E> clazz, final EntityPath<E> entityPath) {
        this.tableName = tableName;
        this.clazz = clazz;
        this.entityPath = entityPath;
    }

    protected JPAQuery<E> createQuery() {
        return new JPAQuery<E>(em);
    }

    public JPAQuery<E> builder() {
        return createQuery().from(entityPath);
    }

    @Transactional
    public Optional<E> findById(@NotNull @NonNull final Long id) {
        return Optional.ofNullable(em.find(clazz, id));
    }

    public Optional<E> findOne(@NotNull @NonNull final Predicate predicate) {
        final JPAQuery<E> query = createQuery().from(entityPath).where(predicate)
            .select(entityPath);
        return Optional.ofNullable(query.fetchOne());
    }

    public E findByIdOrThrowException(@NotNull @NonNull final Long id) throws ApplicationException {
        final Optional<E> entityOpt = findById(id);
        if (entityOpt.isEmpty()) {
            throw new ApplicationException("");
        }
        return entityOpt.get();
    }

    @Transactional
    public E insert(@Valid @NotNull @NonNull final E entity) {
        em.persist(entity);
        return entity;
    }

    @Transactional
    public E save(@Valid @NotNull @NonNull final E entity) {
        return em.merge(entity);
    }

    public List<E> saveMany(@NotNull final List<E> entities) {
        return entities.parallelStream()
            .map(this::save)
            .collect(Collectors.toList());
    }

    @Transactional
    public void delete(@NotNull @NonNull final E entity) {
        em.remove(entity);
    }

    @Transactional
    public void deleteById(final Long id) {
        findById(id).ifPresent(this::delete);
    }

    public long count(@NotNull @NonNull final Predicate predicate) {
        return createQuery().from(entityPath)
            .where(predicate)
            .fetchCount();
    }

    public boolean exists(@NotNull @NonNull final Predicate predicate) {
        return count(predicate) > 0;
    }

    @Transactional
    public void resetSequence() {
        em.createNativeQuery("CALL reset_table_sequence(:table_name)")
            .setParameter("table_name", tableName)
            .executeUpdate();
    }

}
