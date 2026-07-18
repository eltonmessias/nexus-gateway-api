package com.nexus.nexusiam.presentation.dto.response;

public record ApiClientTokenResponse(
        String accessToken,
        String refreshToken,
        String clientId,
        long expiresIn,
        long refreshExpiresIn
) {}
