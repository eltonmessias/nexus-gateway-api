package com.nexus.nexusiam.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TeamRequest(
        @NotBlank String name,
        String description,
        @NotNull UUID organizationId
) {
}
