package com.nexus.nexusiam.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank String organizationName,
        @NotBlank @Size(min = 3, max = 50) String organizationSlug,
        String organizationDescription,
        @NotBlank String ownerName,
        @NotBlank @Email String ownerEmail,
        @NotBlank @Size(min = 8) String ownerPassword
) {
}
