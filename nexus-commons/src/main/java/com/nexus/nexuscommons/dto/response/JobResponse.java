package com.nexus.nexuscommons.dto.response;

import java.time.Instant;
import java.util.UUID;

public record JobResponse(
        UUID id,
        String type,
        String status,
        Object result,
        String errorMessage,
        int attemptCount,
        int maxAttempts,
        Instant createdAt,
        Instant statedAt,
        Instant completedAt
) {
}
