package com.pedrozc90.core.audit;

public interface Auditable {

    Audit getAudit();

    void setAudit(final Audit audit);

}
