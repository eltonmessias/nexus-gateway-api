package com.nexus.nexusiam.presentation.dto.response;

public record ApiClientTokenResponse(
        String accessToken,
        String clientId,
        long expiresIn
) {}
