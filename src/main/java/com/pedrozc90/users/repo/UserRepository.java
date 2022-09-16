package com.pedrozc90.users.repo;

import com.pedrozc90.core.data.CrudRepository;
import com.pedrozc90.users.models.User;
import com.pedrozc90.users.models.UserData;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Singleton;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Singleton
public class UserRepository extends CrudRepository<User, Long> {

    public UserRepository(final EntityManager em) {
        super(em, User.class);
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
    public Optional<User> findByUsername(final String username) {
        try {
            final String queryStr = "SELECT s FROM User as s WHERE s.username = :username";
            final TypedQuery<User> query = em.createQuery(queryStr, User.class)
                .setParameter("username", username);
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

    @Transactional
    public User update(final User user, final UserData data) {
        if (user == null) return null;
        user.setUsername(data.getUsername());
        user.setEmail(data.getEmail());
        user.setProfile(data.getProfile());
        user.setActive(data.isActive());
        user.setAudit(data.getAudit());
        return super.update(user);
    }

}