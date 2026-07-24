package com.nexus.nexusiam.domain.port.out;

import com.nexus.nexusiam.domain.model.CallerContext;

import java.util.UUID;

/**
 * Authorisation boundary. Resolves the current caller and enforces
 * tenant isolation and role requirements. Implemented in infrastructure
 * against the security context.
 */
public interface AuthorizationPort {

    /** The authenticated caller, or throws if there is no valid user principal. */
    CallerContext currentCaller();

    /** Require the caller to be a platform super-admin. Throws {@code ForbiddenException} otherwise. */
    void requirePlatformAdmin();

    /**
     * Read access scoped to a single organisation (multi-tenant isolation).
     * Platform admins bypass. Throws {@code ForbiddenException} otherwise.
     */
    void requireSameOrg(UUID organizationId);

    /**
     * Management access to an organisation: caller must be OWNER/ADMIN of that
     * same organisation. Platform admins bypass. Throws {@code ForbiddenException} otherwise.
     */
    void requireOrgManager(UUID organizationId);

    /**
     * Contributor access to an organisation: caller must be OWNER/ADMIN/DEVELOPER
     * of that same organisation. Used for feature-flag and API-client changes.
     * Platform admins bypass. Throws {@code ForbiddenException} otherwise.
     */
    void requireOrgContributor(UUID organizationId);

    /**
     * Resolve the organisation a list query must be scoped to.
     * <ul>
     *   <li>Platform admin: returns {@code requestedOrganizationId} as-is (may be
     *       {@code null} to list across every organisation).</li>
     *   <li>Any other caller: forced to their own organisation. If a different
     *       {@code requestedOrganizationId} was supplied, throws {@code ForbiddenException}.</li>
     * </ul>
     */
    UUID resolveOrgScope(UUID requestedOrganizationId);
}
