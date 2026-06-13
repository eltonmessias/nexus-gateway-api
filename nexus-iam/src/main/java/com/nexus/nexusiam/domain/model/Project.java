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
public class Project {
    private UUID id;
    private String name;
    private String description;
    private UUID teamId;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean active;
}