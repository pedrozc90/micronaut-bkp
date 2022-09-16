package com.pedrozc90.users.repo;

import com.pedrozc90.core.data.CrudRepository;
import com.pedrozc90.users.models.User;
import com.pedrozc90.users.models.UserData;
import com.pedrozc90.users.models.UserRegistration;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Singleton;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

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
    public Optional<User> findByEmail(final String email) {
        try {
            final String queryStr = "SELECT s FROM User as s WHERE s.email = :email";
            final TypedQuery<User> query = em.createQuery(queryStr, User.class)
                .setParameter("email", email);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @ReadOnly
    public boolean validateEmail(final String email) {
        try {
            final String queryStr = "SELECT 1 FROM User as s WHERE lower(s.email) = lower(:email)";
            final Integer result = em.createQuery(queryStr, Integer.class)
                .setParameter("email", email)
                .getSingleResult();
            return (result == 1);
        } catch (NoResultException e) {
            return false;
        }
    }

    @ReadOnly
    public boolean validateUsername(final String username) {
        try {
            final String queryStr = "SELECT 1 FROM User as s WHERE lower(s.username) = lower(:username)";
            final Integer result = em.createQuery(queryStr, Integer.class)
                .setParameter("username", username)
                .getSingleResult();
            return (result == 1);
        } catch (NoResultException e) {
            return false;
        }
    }

    @ReadOnly
    public List<User> fetch(final int page, final int rpp, final String q) {
        String queryStr = "SELECT s FROM User as s WHERE 1 = 1 ";

        if (StringUtils.isNotBlank(q)) {
            queryStr += "lower(s.email) LIKE :email " +
                "lower(s.username) LIKE :username ";
        }

        queryStr += "ORDER BY s.id ASC";

        final TypedQuery<User> query = em.createQuery(queryStr, User.class);

        if (StringUtils.isNotBlank(q)) {
            final String qSearch = StringUtils.lowerCase("%" + q + "%");
            query.setParameter("email", qSearch)
                .setParameter("username", qSearch);
        }

        return query.setMaxResults(rpp + 1)
            .setFirstResult((page - 1) * rpp)
            .getResultList();
    }

    @ReadOnly
    public Long count(final int page, final int rpp, final String q) {
        String queryStr = "SELECT count(distinct s.id) FROM User as s WHERE 1 = 1 ";

        if (StringUtils.isNotBlank(q)) {
            queryStr += "lower(s.email) LIKE :email " +
                "lower(s.username) LIKE :username ";
        }

        final TypedQuery<Long> query = em.createQuery(queryStr, Long.class);

        if (StringUtils.isNotBlank(q)) {
            final String qSearch = StringUtils.lowerCase("%" + q + "%");
            query.setParameter("email", qSearch)
                .setParameter("username", qSearch);
        }

        return query.getSingleResult();
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

    @Transactional
    public User register(final UserRegistration data) {
        final User user = new User();
        user.setEmail(data.getEmail());
        user.setUsername(data.getUsername());
        user.setPassword(DigestUtils.md5Hex(data.getPassword()));
        user.setPasswordConfirm(DigestUtils.md5Hex(data.getPasswordConfirm()));
        return super.save(user);
    }

}