package com.nexus.nexusiam.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OrganizationRequest(
        @NotBlank String name,
        @NotBlank @Size(min = 3, max = 50) String slug,
        String description
) {
}
