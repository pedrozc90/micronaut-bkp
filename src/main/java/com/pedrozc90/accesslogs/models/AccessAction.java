package com.pedrozc90.accesslogs.models;

public enum AccessAction {

    LOGIN("Login"),
    LOGIN_FAILED("Login Failed"),
    LOGOUT("Logout"),
    LOGIN_REFRESH("Login Refresh"),
    LOGIN_REFRESH_FAILED("Login Refresh Failed");

    private final String description;

    AccessAction(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
