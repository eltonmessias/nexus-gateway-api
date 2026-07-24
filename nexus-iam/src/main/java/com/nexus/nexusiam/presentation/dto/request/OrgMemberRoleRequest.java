package com.nexus.nexusiam.presentation.dto.request;

import com.nexus.nexusiam.domain.model.OrgMemberRole;
import jakarta.validation.constraints.NotNull;

public record OrgMemberRoleRequest(
        @NotNull OrgMemberRole role
) {
}
