package com.nexus.nexusiam.presentation.dto.response;

import com.nexus.nexusiam.domain.model.OrgMember;

import java.time.Instant;
import java.util.UUID;

public record OrgMemberResponse(
        UUID id,
        UUID organizationId,
        UUID userId,
        String name,
        String email,
        String role,
        String status,
        Instant joinedAt,
        Instant updatedAt
) {
    public static OrgMemberResponse from(OrgMember member) {
        return new OrgMemberResponse(
                member.getId(),
                member.getOrganizationId(),
                member.getUserId(),
                member.getName(),
                member.getEmail(),
                member.getRole().name(),
                member.getStatus().name(),
                member.getJoinedAt(),
                member.getUpdatedAt()
        );
    }
}
