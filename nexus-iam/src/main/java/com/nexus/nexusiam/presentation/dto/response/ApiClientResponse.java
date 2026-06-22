package com.nexus.nexusiam.presentation.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ApiClientResponse(
        UUID id,
        String name,
        UUID projectId,
        UUID organizationId,
        String clientId,
        Integer rateLimitRpm,
        Integer rateLimitBurst,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
