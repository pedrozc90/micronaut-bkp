package com.pedrozc90.core.data;

import com.pedrozc90.core.exceptions.ApplicationException;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface CrudRepository<E> {

    Optional<E> findById(final Long id);

    E findByIdOrThrowException(final long id) throws ApplicationException;

    E save(@NotNull final E entity);

    List<E> saveMany(@NotNull final List<E> entities) throws ApplicationException;

    void deleteById(final Long id);

    void delete(@NotNull final E entity);

    void resetSequence();

}
