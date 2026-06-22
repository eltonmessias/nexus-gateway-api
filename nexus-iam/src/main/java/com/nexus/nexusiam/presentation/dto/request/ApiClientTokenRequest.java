package com.nexus.nexusiam.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ApiClientTokenRequest(
        @NotBlank String clientId,
        @NotBlank String apiKey
) {}
