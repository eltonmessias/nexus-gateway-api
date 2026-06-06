package com.nexus.nexuscommons.event;

import java.time.Instant;

public record JobCreatedEvent(
        String jobId,
        String type,
        int priority,
        Instant createdAt
) {
}
