package com.pedrozc90.core.config;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("application")
public class ApplicationConfigurationProperties implements ApplicationConfiguration {

    private static final int DEFAULT_MAX = 10;

    private String env;
    private int max = DEFAULT_MAX;

    @Override
    public String getEnv() {
        return env;
    }

    @Override
    public int getMax() {
        return max;
    }

    public void setMax(final int max) {
        this.max = max;
    }

}
