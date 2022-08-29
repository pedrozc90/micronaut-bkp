package com.pedrozc90.users.models;

public enum Profile {

    MASTER("Master"),
    NORMAL("Normal");

    private final String description;

    Profile(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
