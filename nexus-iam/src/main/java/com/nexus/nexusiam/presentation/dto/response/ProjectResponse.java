package com.nexus.nexusiam.presentation.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        String name,
        String description,
        UUID teamId,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
