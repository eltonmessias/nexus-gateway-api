package com.nexus.nexusiam.presentation.dto.request;

import com.nexus.nexusiam.domain.model.OrgMemberRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrgMemberInviteRequest(
        String name,
        @NotBlank @Email String email,
        @NotNull OrgMemberRole role
) {
}
