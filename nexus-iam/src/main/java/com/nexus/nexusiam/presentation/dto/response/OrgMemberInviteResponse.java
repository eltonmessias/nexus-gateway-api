package com.nexus.nexusiam.presentation.dto.response;

import com.nexus.nexusiam.domain.model.OrgMember;

import java.time.Instant;
import java.util.UUID;

/**
 * Returned when a member is invited. Unlike {@link OrgMemberResponse} (used for
 * listings) this carries the one-time {@code inviteToken} so the caller can build
 * the invitation link — it is never exposed in member lists.
 */
public record OrgMemberInviteResponse(
        UUID id,
        UUID organizationId,
        String name,
        String email,
        String role,
        String status,
        Instant joinedAt,
        String inviteToken
) {
    public static OrgMemberInviteResponse from(OrgMember m) {
        return new OrgMemberInviteResponse(
                m.getId(),
                m.getOrganizationId(),
                m.getName(),
                m.getEmail(),
                m.getRole().name(),
                m.getStatus().name(),
                m.getJoinedAt(),
                m.getInviteToken()
        );
    }
}
