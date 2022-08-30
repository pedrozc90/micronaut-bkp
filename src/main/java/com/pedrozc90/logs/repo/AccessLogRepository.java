package com.pedrozc90.logs.repo;

import com.pedrozc90.core.data.CrudRepository;
import com.pedrozc90.logs.models.AccessLog;
import com.pedrozc90.logs.models.QAccessLog;
import jakarta.inject.Singleton;

@Singleton
public class AccessLogRepository extends CrudRepository<AccessLog> {

    public AccessLogRepository() {
        super("access_logs", AccessLog.class, QAccessLog.accessLog);
    }

}