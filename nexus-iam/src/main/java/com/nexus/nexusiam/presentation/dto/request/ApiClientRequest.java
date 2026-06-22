package com.nexus.nexusiam.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record ApiClientRequest(
        @NotBlank String name,
        @NotNull UUID projectId,
        @NotNull UUID organizationId,
        @Positive Integer rateLimitRpm,
        @Positive Integer rateLimitBurst
) {
}
