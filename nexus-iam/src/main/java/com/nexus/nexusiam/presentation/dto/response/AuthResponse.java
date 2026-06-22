package com.nexus.nexusiam.presentation.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String email,
        long accessTokenExpiresIn,
        long refreshTokenExpiresIn
) {
}
