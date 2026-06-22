package com.nexus.nexusiam.domain.model;

import com.nexus.nexusiam.domain.model.valueobject.Email;
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
public class User {
    private UUID id;
    private Email email;
    private String name;
    private String passwordHash;
    private UUID organizationId;
    private Role role;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean active;
}
