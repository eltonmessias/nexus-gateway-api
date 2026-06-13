package com.nexus.nexusiam.presentation.dto.response;

import java.time.Instant;
import java.util.UUID;

public record TeamResponse(
        UUID id,
        String name,
        String description,
        UUID organizationId,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
