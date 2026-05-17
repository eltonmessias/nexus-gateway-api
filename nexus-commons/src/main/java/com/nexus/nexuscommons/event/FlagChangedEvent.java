package com.nexus.nexuscommons.event;

import java.time.Instant;
import java.util.UUID;

public record FlagChangedEvent(
        String flagKey,
        String environment,
        String changeType,
        Instant changedAt
) {
    public static FlagChangedEvent of(String flagKey, String environment, String changeType) {
        return new FlagChangedEvent(flagKey, environment, changeType, Instant.now());
    }
}
