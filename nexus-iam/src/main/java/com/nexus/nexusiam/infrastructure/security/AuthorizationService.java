package com.nexus.nexusiam.infrastructure.security;

import com.nexus.nexusiam.domain.exception.ForbiddenException;
import com.nexus.nexusiam.domain.model.CallerContext;
import com.nexus.nexusiam.domain.model.User;
import com.nexus.nexusiam.domain.port.out.AuthorizationPort;
import com.nexus.nexusiam.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Resolves the caller from the Spring Security context and enforces
 * tenant isolation and role requirements. Only user principals (not API
 * clients) can pass these checks — an API-client token has no backing user.
 */
@Service
@RequiredArgsConstructor
public class AuthorizationService implements AuthorizationPort {

    private final UserRepository userRepository;

    @Override
    public CallerContext currentCaller() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
            throw new ForbiddenException("Not authenticated");
        }
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ForbiddenException("Caller is not a recognised user"));
        return new CallerContext(user.getId(), user.getOrganizationId(), user.getRole());
    }

    @Override
    public void requirePlatformAdmin() {
        if (!currentCaller().isPlatformAdmin()) {
            throw new ForbiddenException("This action requires platform administrator privileges");
        }
    }

    @Override
    public void requireSameOrg(UUID organizationId) {
        CallerContext caller = currentCaller();
        if (caller.isPlatformAdmin()) return;
        if (organizationId == null || !organizationId.equals(caller.organizationId())) {
            throw new ForbiddenException("You cannot access resources of another organisation");
        }
    }

    @Override
    public void requireOrgManager(UUID organizationId) {
        CallerContext caller = currentCaller();
        if (caller.isPlatformAdmin()) return;
        if (organizationId == null || !organizationId.equals(caller.organizationId())) {
            throw new ForbiddenException("You cannot manage another organisation");
        }
        if (!caller.isOrgManager()) {
            throw new ForbiddenException("This action requires an organisation OWNER or ADMIN role");
        }
    }

    @Override
    public void requireOrgContributor(UUID organizationId) {
        CallerContext caller = currentCaller();
        if (caller.isPlatformAdmin()) return;
        if (organizationId == null || !organizationId.equals(caller.organizationId())) {
            throw new ForbiddenException("You cannot modify resources of another organisation");
        }
        if (!caller.isOrgContributor()) {
            throw new ForbiddenException("This action requires an organisation OWNER, ADMIN or DEVELOPER role");
        }
    }

    @Override
    public UUID resolveOrgScope(UUID requestedOrganizationId) {
        CallerContext caller = currentCaller();
        if (caller.isPlatformAdmin()) {
            return requestedOrganizationId; // null = list across all orgs, admin only
        }
        // A tenant with no organisation must never fall through to a global listing.
        if (caller.organizationId() == null) {
            throw new ForbiddenException("Your account is not associated with an organisation");
        }
        if (requestedOrganizationId != null && !requestedOrganizationId.equals(caller.organizationId())) {
            throw new ForbiddenException("You cannot access resources of another organisation");
        }
        return caller.organizationId();
    }
}
