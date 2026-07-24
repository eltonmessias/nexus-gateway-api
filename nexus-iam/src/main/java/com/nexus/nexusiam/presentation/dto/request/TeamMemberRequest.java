package com.nexus.nexusiam.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TeamMemberRequest(
        @NotNull UUID orgMemberId
) {
}
