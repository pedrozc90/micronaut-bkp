package com.pedrozc90.core.data;

import com.pedrozc90.core.exceptions.ApplicationException;
import com.pedrozc90.core.querydsl.JPAQuery;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPADeleteClause;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.transaction.annotation.ReadOnly;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
public abstract class EntityRepository<E, ID> implements CrudRepository<E, ID> {

    @PersistenceContext
    protected EntityManager em;

    protected final String tableName;
    protected final Class<E> clazz;
    protected final EntityPath<E> entity;

    public EntityRepository(final String tableName, final Class<E> clazz, final EntityPath<E> entity) {
        this.tableName = tableName;
        this.clazz = clazz;
        this.entity = entity;
    }

    protected EntityPath<E> entity() {
        return this.entity;
    }

    protected JPAQuery<E> createQuery() {
        return new JPAQuery<E>(em);
    }

    public JPAQuery<E> builder() {
        return createQuery().from(entity);
    }

    @Override
    @ReadOnly
    public Optional<E> findById(@Nullable ID id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(em.find(clazz, id));
    }

    public E findByIdOrThrowException(@Nullable final ID id) throws ApplicationException {
        final Optional<E> entityOpt = findById(id);
        if (entityOpt.isEmpty()) {
            if (id == null) {
                throw ApplicationException.of("%s not found.", clazz.getSimpleName().toLowerCase()).notFound();
            }
            throw ApplicationException.of("%s (id: %d) not found.", clazz.getSimpleName().toLowerCase(), id).notFound();
        }
        return entityOpt.get();
    }

    @ReadOnly
    public Optional<E> findOne(@NotNull @NonNull final Predicate predicate) {
        final JPAQuery<E> query = createQuery().from(entity).where(predicate)
            .select(entity)
            .limit(1);
        return Optional.ofNullable(query.fetchOne());
    }

    @ReadOnly
    public Iterable<E> findMany(@Nullable final Predicate predicate) {
        final JPAQuery<E> query = createQuery().from(entity);
        if (predicate != null) {
            query.where(predicate);
        }
        return query.select(entity).fetch();
    }

    @Override
    @ReadOnly
    public Iterable<E> findAll() {
        return findMany(null);
    }

    @ReadOnly
    public long count(@Nullable final Predicate predicate) {
        final JPAQuery<E> query = createQuery().from(entity);
        if (predicate != null) {
            query.where(predicate);
        }
        return query.fetchCount();
    }

    @Override
    @ReadOnly
    public long count() {
        return count(null);
    }

    public boolean exists(@Nullable final Predicate predicate) {
        return count(predicate) > 0;
    }

    @Override
    @ReadOnly
    public boolean existsById(@Nullable ID id) {
        return findById(id).isPresent();
    }

    @Override
    @Transactional
    public <S extends E> S save(@Valid @NotNull @NonNull S entity) {
        em.persist(entity);
        return entity;
    }

    @Override
    @Transactional
    public <S extends E> Iterable<S> saveAll(@Valid @NotNull @NonNull Iterable<S> entities) {
        return StreamSupport.stream(entities.spliterator(), false)
            .map(this::save)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public <S extends E> S update(@Valid @NotNull @NonNull S entity) {
        return em.merge(entity);
    }

    @Override
    @Transactional
    public <S extends E> Iterable<S> updateAll(@Valid @NotNull @NonNull Iterable<S> entities) {
        return StreamSupport.stream(entities.spliterator(), false)
            .map(this::update)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(@Nullable ID id) {
        if (id == null) return;
        findById(id).ifPresent(this::delete);
    }

    @Override
    @Transactional
    public void delete(@NotNull @NonNull E entity) {
        em.remove(entity);
    }

    @Override
    @Transactional
    public void deleteAll(@NotNull @NonNull Iterable<? extends E> entities) {
        entities.forEach(this::delete);
    }

    @Override
    @Transactional
    public void deleteAll() {
        final long deleted = new JPADeleteClause(em, entity).execute();
        if (deleted > 0) {
            log.info("{} rows deleted. ", deleted);
        }
    }

    @Transactional
    public void resetSequence() {
        em.createNativeQuery("CALL reset_table_sequence(:table_name)")
            .setParameter("table_name", tableName)
            .executeUpdate();
    }

    @Transactional
    public void flush() {
        em.flush();
    }

    public void detach(final Object entity) {
        em.detach(entity);
    }

    @Transactional
    public void commit() {
        em.getTransaction().commit();
    }

}
