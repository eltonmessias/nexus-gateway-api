package com.nexus.nexuscommons.event;

import java.time.Instant;
import java.util.UUID;

public record JobCompletedEvent(
        UUID jobId,
        String type,
        String status,
        String result,
        String errorMessage,
        Instant completedAt
) {
    public static JobCompletedEvent success(UUID jobId, String type, String result) {
        return new JobCompletedEvent(jobId, type, "DONE", result, null, Instant.now());
    }

    public static JobCompletedEvent failure(UUID jobId, String type, String errorMessage) {
        return new JobCompletedEvent(jobId, type, "FAILED", errorMessage, null, Instant.now());
    }
}
