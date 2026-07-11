package com.nexus.nexusiam.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditLog {
    private UUID id;
    private String action;
    private String actorId;
    private ActorType actorType;
    private UUID organizationId;
    private String resourceType;
    private String resourceId;
    private Map<String, Object> metadata;
    private String ipAddress;
    private Instant createdAt;
}
