package com.pedrozc90.logs.repo;

import com.pedrozc90.core.data.EntityRepository;
import com.pedrozc90.logs.models.QRefreshToken;
import com.pedrozc90.logs.models.RefreshToken;
import com.querydsl.jpa.impl.JPAUpdateClause;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Singleton
public class RefreshTokenRepository extends EntityRepository<RefreshToken, Long> {

    public RefreshTokenRepository() {
        super("access_logs", RefreshToken.class, QRefreshToken.refreshToken1);
    }

    @ReadOnly
    public Optional<RefreshToken> findOne(final String refreshToken) {
        return findOne(QRefreshToken.refreshToken1.refreshToken.eq(refreshToken));
    }

    @Transactional
    public RefreshToken register(final String username, final String refreshToken) {
        final RefreshToken rt = new RefreshToken();
        rt.setUsername(username);
        rt.setRefreshToken(refreshToken);
        return save(rt);
    }

    @Transactional
    public void revokeOld(final String username) {
        final List<Long> ids = createQuery().from(entity)
            .where(QRefreshToken.refreshToken1.username.eq(username))
            .where(QRefreshToken.refreshToken1.revoked.isFalse())
            .orderBy(QRefreshToken.refreshToken1.id.desc())
            // keep the last one inserted
            .offset(1)
            .select(QRefreshToken.refreshToken1.id)
            .fetch();

        long updated = new JPAUpdateClause(em, entity)
            .set(QRefreshToken.refreshToken1.revoked, true)
            .where(QRefreshToken.refreshToken1.id.in(ids))
            .execute();

        if (updated > 0) {
            log.info("{} rows revoked.", updated);
        }

    }

}