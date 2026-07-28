package com.nexus.nexusiam.presentation.dto.response;

/** Public view of an invitation, shown on the accept-invite page. */
public record InvitationDetailsResponse(
        String organizationName,
        String email,
        String name,
        String role
) {}
