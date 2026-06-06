package com.nexus.nexuscommons.event;

import java.time.Instant;

public record JobCompletedEvent(
        String jobId,
        String status,
        String errorMessage,
        Instant completedAt
) {
}
