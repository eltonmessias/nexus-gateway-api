package com.nexus.nexusiam.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProjectRequest (
        @NotBlank String name,
        @NotBlank String key,
        String description,
        @NotNull UUID organizationId,
        UUID teamId
        ){
}
