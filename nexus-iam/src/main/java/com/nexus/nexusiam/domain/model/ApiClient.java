package com.nexus.nexusiam.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiClient {
    private UUID id;
    private String name;
    private UUID projectId;
    private UUID organizationId;
    private String clientId;
    private String apiKeyHash;
    private Integer rateLimitRpm;
    private Integer rateLimitBurst;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
