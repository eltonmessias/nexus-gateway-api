package com.nexus.nexusfeatureflags.model;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public class Flag {
    @Id
    private UUID id;
    private String flagKey;
    private String value = "default";
    private String environment;
    private boolean enabled;

    public Flag(String flagKey, String value, String environment, boolean enabled) {
        this.flagKey = flagKey;
        this.value = value;
        this.environment = environment;
        this.enabled = enabled;
    }
    public Flag(){}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFlagKey() {
        return flagKey;
    }

    public void setFlagKey(String flagKey) {
        this.flagKey = flagKey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
