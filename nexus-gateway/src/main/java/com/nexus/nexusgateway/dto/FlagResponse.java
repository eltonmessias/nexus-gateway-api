package com.nexus.nexusgateway.dto;

import com.nexus.nexusfeatureflags.model.Flag;

import java.time.Instant;
import java.util.UUID;

public record FlagResponse(
        UUID id,
        String key,
        String description,
        UUID projectId,
        boolean enabled,
        Instant createdAt,
        Instant updatedAt
) {
    public static FlagResponse from(Flag flag) {
        return new FlagResponse(
                flag.getId(),
                flag.getKey(),
                flag.getDescription(),
                flag.getProjectId(),
                flag.isEnabled(),
                flag.getCreatedAt(),
                flag.getUpdatedAt()
        );
    }
}
