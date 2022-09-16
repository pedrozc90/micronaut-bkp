package com.pedrozc90.core.data;

import com.pedrozc90.core.exceptions.ApplicationException;
import io.micronaut.transaction.annotation.ReadOnly;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class CrudRepository<E, ID> {

    protected EntityManager em;
    protected Class<E> clazz;

    public CrudRepository(final EntityManager em, final Class<E> clazz) {
        this.em = em;
        this.clazz = clazz;
    }

    @ReadOnly
    public Optional<E> findById(final ID id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(em.find(clazz, id));
    }

    public E findByIdOrThrowException(final ID id) throws ApplicationException {
        return findById(id).orElseThrow(() -> {
            if (id == null) {
                return ApplicationException.of("%s not found.", clazz.getSimpleName().toLowerCase()).notFound();
            }
            return ApplicationException.of("%s (id: %d) not found.", clazz.getSimpleName().toLowerCase(), id).notFound();
        });
    }

    @Transactional
    public E save(@NotNull final E entity) {
        em.persist(entity);
        return entity;
    }

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
