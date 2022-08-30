package com.pedrozc90.core.authentication;

public class AuthenticationContext {

    private static final ThreadLocal<Long> tenants = new ThreadLocal<>();
    private static final ThreadLocal<Long> users = new ThreadLocal<>();

    public static Long getTenantId() {
        return tenants.get();
    }

    public static void setTenantId(final Long tenantId) {
        tenants.set(tenantId);
    }

    public static Long getUserId() {
        return users.get();
    }

    public static void setUserId(final Long userId) {
        users.set(userId);
    }

}
