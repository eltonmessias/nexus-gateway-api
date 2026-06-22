package com.nexus.nexusiam.presentation.dto.response;

import com.nexus.nexusiam.domain.model.Role;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        UUID organizationId,
        Role role,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
