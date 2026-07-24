package com.nexus.nexusiam.domain.model;

import java.util.UUID;

/**
 * Identity of the authenticated principal making the current request.
 * Resolved from the security context by an {@link com.nexus.nexusiam.domain.port.out.AuthorizationPort}.
 */
public record CallerContext(UUID userId, UUID organizationId, Role role) {

    /** Platform super-admin — may operate across every organisation (dashboard). */
    public boolean isPlatformAdmin() {
        return role == Role.ADMIN;
    }

    /** Roles allowed to manage members, teams, projects and settings within their own organisation. */
    public boolean isOrgManager() {
        return role == Role.ORG_OWNER || role == Role.TEAM_ADMIN;
    }

    /**
     * Roles allowed to contribute operational resources (feature flags, API
     * clients) within their own organisation — managers plus developers.
     */
    public boolean isOrgContributor() {
        return isOrgManager() || role == Role.TEAM_MEMBER;
    }
}
