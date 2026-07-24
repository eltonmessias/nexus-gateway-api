package com.nexus.nexusiam.presentation.dto.response;

import com.nexus.nexusiam.domain.model.OrgMemberRole;

import java.time.Instant;
import java.util.UUID;

public record TeamMemberResponse(
        UUID id,
        UUID teamId,
        UUID orgMemberId,
        String memberName,
        String memberEmail,
        OrgMemberRole memberRole,
        Instant joinedAt
) {
}
