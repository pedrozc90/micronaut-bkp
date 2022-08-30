package com.pedrozc90.core.utils;

import com.pedrozc90.tenants.models.Tenant;
import com.pedrozc90.users.models.User;
import io.micronaut.security.authentication.Authentication;

import java.util.Map;

public class AuthenticationUtils {

    public static final String USER = "user_id";
    public static final String TENANT = "tenant_id";

    // USER
    public static Map<String, Object> setUserId(final Long userId, final Map<String, Object> attrs) {
        attrs.put(USER, userId);
        return attrs;
    }

    public static Map<String, Object> setUserId(final User user, final Map<String, Object> attrs) {
        if (user == null) return attrs;
        return setUserId(user.getId(), attrs);
    }

    public static Long getUserId(final Authentication authentication) {
        return getUserId(authentication.getAttributes());
    }

    public static Long getUserId(final Map<String, Object> attrs) {
        return (Long) getAttribute(attrs, USER);
    }

    private static Object getAttribute(final Map<String, Object> attrs, final String key) {
        if (attrs == null) return null;
        return attrs.getOrDefault(key, null);
    }

    // TENANT
    public static Map<String, Object> setTenantId(final Long tenantId, final Map<String, Object> attrs) {
        attrs.put(TENANT, tenantId);
        return attrs;
    }

    public static Map<String, Object> setTenantId(final Tenant tenant, final Map<String, Object> attrs) {
        if (tenant == null) return attrs;
        return setTenantId(tenant.getId(), attrs);
    }

    public static Long getTenantId(final Authentication authentication) {
        return getTenantId(authentication.getAttributes());
    }

    public static Long getTenantId(final Map<String, Object> attrs) {
        return (Long) getAttribute(attrs, TENANT);
    }

}
