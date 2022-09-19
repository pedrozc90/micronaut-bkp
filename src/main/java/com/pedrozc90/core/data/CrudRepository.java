package com.pedrozc90.core.data;

import com.pedrozc90.core.exceptions.ApplicationException;
import com.pedrozc90.core.querydsl.JPAQuery;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import io.micronaut.transaction.annotation.ReadOnly;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class CrudRepository<E, ID> {

    protected EntityManager em;
    protected Class<E> clazz;
    protected EntityPath<E> entity;

    public CrudRepository(final EntityManager em, final Class<E> clazz, final EntityPath<E> entity) {
        this.em = em;
        this.clazz = clazz;
        this.entity = entity;
    }

    public JPAQuery<E> createQuery() {
        return new JPAQuery<E>(em);
    }

    public JPAQuery<E> builder() {
        return createQuery().from(entity);
    }

    @ReadOnly
    public Optional<E> findById(final ID id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(em.find(clazz, id));
    }

    @ReadOnly
    public E findByIdOrThrowException(final ID id) throws ApplicationException {
        return findById(id).orElseThrow(() -> {
            if (id == null) {
                return ApplicationException.of("%s not found.", clazz.getSimpleName().toLowerCase()).notFound();
            }
            return ApplicationException.of("%s (id: %d) not found.", clazz.getSimpleName().toLowerCase(), id).notFound();
        });
    }

    @ReadOnly
    public Optional<E> findOne(final Predicate predicate) {
        final JPAQuery<E> query = createQuery().from(entity);
        if (predicate != null) {
            query.where(predicate);
        }
        return Optional.ofNullable(query
            .select(entity)
            .limit(1)
            .fetchOne()
        );
    }

    @ReadOnly
    public long count(final Predicate predicate) {
        final JPAQuery<E> query = createQuery().from(entity);
        if (predicate != null) {
            query.where(predicate);
        }
        return query.fetchCount();
    }

    @ReadOnly
    public long count() {
        return count(null);
    }

    @ReadOnly
    public boolean exists(final Predicate predicate) {
        return count(predicate) > 0;
    }

    @ReadOnly
    public boolean exists() {
        return count(null) > 0;
    }

    @Transactional
    public E save(@Valid @NotNull final E entity) {
        em.persist(entity);
        return entity;
    }

    @Transactional
    public List<E> saveMany(@NotNull final List<E> entities) throws ApplicationException {
        return entities.stream()
            .filter(Objects::nonNull)
            .map(this::save)
            .collect(Collectors.toList());
    }

    @Transactional
    public E update(@NotNull final E entity) {
        return em.merge(entity);
    }

    @Transactional
    public void removeById(final ID id) {
        findById(id).ifPresent(this::remove);
    }

    @Transactional
    public void remove(@NotNull @NotNull final E entity) {
        em.remove(entity);
    }

}
