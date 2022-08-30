package com.pedrozc90.core.events;

import com.pedrozc90.core.authentication.AuthenticationContext;
import com.pedrozc90.core.utils.AuthenticationUtils;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.security.authentication.Authentication;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Singleton
public class AuthenticationValidatedEventListener implements ApplicationEventListener<AuthenticationValidatedEvent> {

    @Override
    public void onApplicationEvent(final AuthenticationValidatedEvent event) {
        final Authentication authentication = (Authentication) event.getSource();
        final Map<String, Object> attributes = authentication.getAttributes();

        final Long userId = AuthenticationUtils.getUserId(attributes);
        AuthenticationContext.setUserId(userId);

        final Long tenantId = AuthenticationUtils.getTenantId(attributes);
        AuthenticationContext.setTenantId(tenantId);

        log.info("authenticated -> username: {}, user_id: {}, tenant_id: {}", authentication.getName(), userId, tenantId);
    }

    @Override
    public boolean supports(final AuthenticationValidatedEvent event) {
        return ApplicationEventListener.super.supports(event);
    }

}
