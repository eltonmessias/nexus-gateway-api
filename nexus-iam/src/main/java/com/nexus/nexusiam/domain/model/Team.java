package com.nexus.nexusiam.domain.model;


import java.time.Instant;
import java.util.UUID;

public class Team {
    private UUID id;
    private String name;
    private String description;
    private UUID organizationId;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean active;
}
