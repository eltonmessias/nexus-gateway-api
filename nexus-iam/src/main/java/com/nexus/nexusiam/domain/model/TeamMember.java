package com.nexus.nexusiam.domain.model;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamMember {
    private UUID id;
    private UUID teamId;
    private UUID orgMemberId;
    private String memberName;
    private String memberEmail;
    private OrgMemberRole memberRole;
    private Instant joinedAt;
}
