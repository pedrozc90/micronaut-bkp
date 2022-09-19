package com.pedrozc90.users.repo;

import com.pedrozc90.core.data.CrudRepository;
import com.pedrozc90.core.models.Page;
import com.pedrozc90.core.querydsl.JPAQuery;
import com.pedrozc90.users.models.QUser;
import com.pedrozc90.users.models.User;
import com.pedrozc90.users.models.UserData;
import com.pedrozc90.users.models.UserRegistration;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Singleton;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Optional;

@Singleton
public class UserRepository extends CrudRepository<User, Long> {

    public UserRepository(final EntityManager em) {
        super(em, User.class, QUser.user);
    }

    @ReadOnly
    public Optional<User> findByCredentials(final String username, final String password) {
        return findOne(QUser.user.username.eq(username).and(QUser.user.password.eq(password)));
    }

    @ReadOnly
    public Optional<User> findByUsername(final String username) {
        return findOne(QUser.user.username.eq(username));
    }

    @ReadOnly
    public Optional<User> findByEmail(final String email) {
        return findOne(QUser.user.username.eq(email));
    }

    @ReadOnly
    public Page<User> fetch(final int page, final int rpp, final String q, final Long tenantId) {
        final JPAQuery<User> query = createQuery().from(QUser.user);

        if (StringUtils.isNotBlank(q)) {
            query.where(QUser.user.email.containsIgnoreCase(q))
                .where(QUser.user.username.containsIgnoreCase(q));
        }

        if (tenantId != null) {
            query.where(QUser.user.tenant().id.eq(tenantId));
        }

        query.orderBy(QUser.user.username.asc());

        return Page.create(query, page, rpp);
    }

    @ReadOnly
    public boolean validateEmail(final String email) {
        return exists(QUser.user.email.equalsIgnoreCase(email));
    }

    @ReadOnly
    public boolean validateUsername(final String username) {
        return exists(QUser.user.email.equalsIgnoreCase(username));
    }

    @Transactional
    public User update(final User user, final UserData data) {
        if (user == null) return null;
        user.setUsername(data.getUsername());
        user.setEmail(data.getEmail());
        user.setProfile(data.getProfile());
        user.setActive(data.isActive());
        user.setTenant(data.getTenant());
        return super.update(user);
    }

    @Transactional
    public User register(final UserRegistration data) {
        final User user = new User();
        user.setEmail(data.getEmail());
        user.setUsername(data.getUsername());
        user.setPassword(DigestUtils.md5Hex(data.getPassword()));
        user.setPasswordConfirm(DigestUtils.md5Hex(data.getPasswordConfirm()));
        user.setTenant(data.getTenant());
        return super.save(user);
    }

}