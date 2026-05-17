package com.nexus.nexuscommons.event;

import java.time.Instant;
import java.util.UUID;

public record JobCreatedEvent(
        UUID jobId,
        String type,
        String payload,
        int priority,
        Instant createdAt
) {
    public static JobCreatedEvent of(UUID jobId, String type, String payload, int priority){
        return new JobCreatedEvent(jobId, type, payload, priority, Instant.now());
    }
}
