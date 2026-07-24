package com.nexus.nexusgateway.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record FlagRequest(
        @NotBlank @Pattern(regexp = "^[a-z0-9-_]+$", message = "Key must be lowercase letters, numbers, dashes or underscores")
        String key,
        String description,
        @NotNull UUID projectId,
        Boolean enabled
) {
}
