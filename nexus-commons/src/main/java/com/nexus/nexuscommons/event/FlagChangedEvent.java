package com.nexus.nexuscommons.event;

import java.time.Instant;

public record FlagChangedEvent(
        String flagKey,
        String environment,
        String newValue,
        String reason,
        Instant changedAt
) {
}
