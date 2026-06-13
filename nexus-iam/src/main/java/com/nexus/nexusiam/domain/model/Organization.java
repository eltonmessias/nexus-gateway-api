package com.nexus.nexusiam.domain.model;


import com.nexus.nexusiam.domain.model.valueobject.Slug;
import lombok.*;

import java.time.Instant;
import java.util.UUID;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Organization {
    private UUID id;
    private String name;
    private Slug slug;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean active;
}