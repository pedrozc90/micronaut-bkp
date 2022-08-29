package com.pedrozc90.core.events;

import io.micronaut.security.event.TokenValidatedEvent;

public class AuthenticationValidatedEvent extends TokenValidatedEvent {

    public AuthenticationValidatedEvent(final Object source) {
        super(source);
    }

}
