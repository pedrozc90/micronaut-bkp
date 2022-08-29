package com.pedrozc90.core.audit.listeners;

import com.pedrozc90.core.audit.Audit;
import com.pedrozc90.core.audit.Auditable;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.ZonedDateTime;

public class AuditListener {

    @PrePersist
    public void beforeInsert(final Object entity) {
        if (!(entity instanceof Auditable)) return;
        final ZonedDateTime now = ZonedDateTime.now();
        final Auditable auditable = (Auditable) entity;
        final Audit audit = (auditable.getAudit() != null) ? auditable.getAudit() : new Audit();
        audit.setInsertedAt(now);
        audit.setUpdatedAt(now);
        audit.setVersion((audit.getVersion() != null) ? audit.getVersion() : 1);
        auditable.setAudit(audit);
    }

    @PreUpdate
    public void beforeUpdate(final Object entity) {
        if (!(entity instanceof Auditable)) return;
        final Auditable auditable = (Auditable) entity;
        final Audit audit = auditable.getAudit();
        audit.setUpdatedAt(ZonedDateTime.now());
        auditable.setAudit(audit);
        audit.setVersion(audit.getVersion() + 1);
    }

}
