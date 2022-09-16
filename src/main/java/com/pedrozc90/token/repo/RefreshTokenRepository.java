package com.pedrozc90.token.repo;

import com.pedrozc90.core.data.CrudRepository;
import com.pedrozc90.token.models.RefreshToken;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@Singleton
public class RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    public RefreshTokenRepository(final EntityManager em) {
        super(em, RefreshToken.class);
    }

    @ReadOnly
    public Optional<RefreshToken> findByRefreshToken(final String refreshToken) {
        try {
            final String queryStr = "SELECT rt FROM RefreshToken as rt WHERE rt.refreshToken = :refresh_token";
            final TypedQuery<RefreshToken> query = em.createQuery(queryStr, RefreshToken.class)
                .setParameter("refresh_token", refreshToken);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
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

}