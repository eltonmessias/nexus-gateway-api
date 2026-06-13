package com.nexus.nexusiam.presentation.dto.response;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        UUID organizationId,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
