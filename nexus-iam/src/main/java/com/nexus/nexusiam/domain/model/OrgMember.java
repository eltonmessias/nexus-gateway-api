package com.nexus.nexusiam.domain.model;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrgMember {
    private UUID id;
    private UUID organizationId;
    private UUID userId;
    private String name;
    private String email;
    private OrgMemberRole role;
    private OrgMemberStatus status;
    private Instant joinedAt;
    private Instant updatedAt;
}
