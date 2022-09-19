package com.pedrozc90.token.repo;

import com.pedrozc90.core.data.CrudRepository;
import com.pedrozc90.core.querydsl.JPAQuery;
import com.pedrozc90.token.models.QAccessToken;
import com.pedrozc90.token.models.QRefreshToken;
import com.pedrozc90.token.models.RefreshToken;
import com.querydsl.jpa.impl.JPAUpdateClause;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Singleton
public class RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    public RefreshTokenRepository(final EntityManager em) {
        super(em, RefreshToken.class, QRefreshToken.refreshToken1);
    }

    @ReadOnly
    public Optional<RefreshToken> findByRefreshToken(final String refreshToken) {
        return findOne(QRefreshToken.refreshToken1.refreshToken.eq(refreshToken));
    }

    @Transactional
    public RefreshToken register(final String username, final String refreshToken) {
        try {
            final RefreshToken rt = new RefreshToken();
            rt.setUsername(username);
            rt.setRefreshToken(refreshToken);
            return super.save(rt);
        } finally {
            revoke(username);
        }
    }

    @Transactional
    public void revoke(final String username) {
        final String queryStr = "UPDATE RefreshToken rt " +
            "SET rt.revoked = true " +
            "WHERE rt.revoked = false AND rt.username = :username";
        final int updated = em.createQuery(queryStr)
            .setParameter("username", username)
            .executeUpdate();

        log.debug("{} rows revoked.", updated);
    }

    @Transactional
    public void revoke(final String username, final String accessToken) {
        final List<String> refreshTokens = new JPAQuery<>(em).from(QAccessToken.accessToken1)
            .where(QAccessToken.accessToken1.accessToken.eq(accessToken))
            .where(QAccessToken.accessToken1.username.eq(username))
            .select(QAccessToken.accessToken1.refreshToken)
            .fetch();
        final long updated = new JPAUpdateClause(em, QRefreshToken.refreshToken1)
            .set(QRefreshToken.refreshToken1.revoked, Boolean.TRUE)
            .where(QRefreshToken.refreshToken1.revoked.isFalse())
            .where(QRefreshToken.refreshToken1.username.eq(username))
            .where(QRefreshToken.refreshToken1.refreshToken.in(refreshTokens))
            .execute();
        log.debug("{} rows revoked.", updated);
    }

}