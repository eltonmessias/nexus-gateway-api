package com.nexus.nexusiam.domain.model;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Team {
    private UUID id;
    private String name;
    private String description;
    private UUID organizationId;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean active;
}
