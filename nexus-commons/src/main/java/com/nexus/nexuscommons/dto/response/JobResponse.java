package com.nexus.nexuscommons.dto.response;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record JobResponse(
        UUID id,
        String type,
        String status,
        Map<String, Object> payload,
        UUID organizationId,
        int retries,
        int maxRetries,
        Instant createdAt,
        Instant updatedAt
) {
}
