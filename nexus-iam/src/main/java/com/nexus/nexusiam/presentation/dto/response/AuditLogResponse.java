package com.nexus.nexusiam.presentation.dto.response;

import com.nexus.nexusiam.infrastructure.persistence.entity.AuditLogEntity;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record AuditLogResponse(
        UUID id,
        Instant timestamp,
        String actorId,
        String actorName,
        String actorType,
        String action,
        String resourceType,
        String resourceId,
        UUID organizationId,
        String ipAddress,
        Map<String, Object> metadata
) {
    public static AuditLogResponse from(AuditLogEntity e) {
        return new AuditLogResponse(
                e.getId(),
                e.getCreatedAt(),
                e.getActorId(),
                e.getActorName(),
                e.getActorType() != null ? e.getActorType().name() : null,
                e.getAction(),
                e.getResourceType(),
                e.getResourceId(),
                e.getOrganizationId(),
                e.getIpAddress(),
                e.getMetadata()
        );
    }
}
