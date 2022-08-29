package com.pedrozc90.users.repo;

import com.pedrozc90.core.data.CrudRepository;
import com.pedrozc90.core.exceptions.ApplicationException;
import com.pedrozc90.users.models.User;
import com.pedrozc90.users.models.UserData;
import io.micronaut.runtime.ApplicationConfiguration;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Singleton;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Singleton
public class UserRepository implements CrudRepository<User> {

    private static final List<String> VALID_PROPERTIES_NAMES = Arrays.asList("id", "username", "password", "email", "profile");

    private final EntityManager em;
    private final ApplicationConfiguration config;

    public UserRepository(final EntityManager em, final ApplicationConfiguration config) {
        this.em = em;
        this.config = config;
    }

    @Override
    @ReadOnly
    public Optional<User> findById(final Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    public User findByIdOrThrowException(long id) throws ApplicationException {
        final Optional<User> opt = findById(id);
        if (opt.isEmpty()) {
            throw ApplicationException.of("User (id: %d) not found.", id).notFound();
        }
        return opt.get();
    }

    @ReadOnly
    public Optional<User> findByCredentials(final String username, final String password) {
        try {
            final String queryStr = "SELECT s FROM User as s WHERE s.username = :username AND s.password = :password";
            final TypedQuery<User> query = em.createQuery(queryStr, User.class)
                .setParameter("username", username)
                .setParameter("password", password);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @ReadOnly
    public List<User> fetch() {
        final String queryStr = "SELECT s FROM User as s ORDER BY s.id ASC";
        final TypedQuery<User> query = em.createQuery(queryStr, User.class);
        return query.getResultList();
    }

    @Override
    @Transactional
    public User save(final User entity) {
        em.persist(entity);
        return entity;
    }

    @Override
    @Transactional
    public List<User> saveMany(final List<User> entities) throws ApplicationException {
        throw new ApplicationException("Method Not Implemented");
    }

    @Override
    @Transactional
    public void deleteById(final Long id) {
        findById(id).ifPresent(em::remove);
    }

    @Override
    @Transactional
    public void delete(final User entity) {
        em.remove(entity);
    }

    @Transactional
    public User update(final User user, final UserData data) {
        if (user == null) return null;
        user.setUsername(data.getUsername());
        user.setEmail(data.getEmail());
        user.setProfile(data.getProfile());
        user.setActive(data.isActive());
        user.setAudit(data.getAudit());
        return em.merge(user);
    }

    @Transactional
    public void resetSequence() {
        em.createNamedQuery("reset_sequence").executeUpdate();
    }

}