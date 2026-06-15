package com.nexus.nexusiam.presentation.dto.response;

public record AuthResponse(
        String token,
        String email,
        long expiresIn
) {
}
