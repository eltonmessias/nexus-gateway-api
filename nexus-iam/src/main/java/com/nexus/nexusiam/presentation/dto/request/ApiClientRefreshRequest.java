package com.nexus.nexusiam.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ApiClientRefreshRequest(
        @NotBlank String refreshToken
) {}
