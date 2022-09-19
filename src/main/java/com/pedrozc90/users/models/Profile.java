package com.pedrozc90.users.models;

import java.util.Collections;
import java.util.List;

public enum Profile {

    MASTER("Master", Collections.singletonList("MASTER")),
    NORMAL("Normal");

    private final String description;
    private final List<String> roles;

    Profile(final String description) {
        this(description, Collections.emptyList());
    }

    Profile(final String description, final List<String> roles) {
        this.description = description;
        this.roles = roles;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getRoles() {
        return roles;
    }

}
