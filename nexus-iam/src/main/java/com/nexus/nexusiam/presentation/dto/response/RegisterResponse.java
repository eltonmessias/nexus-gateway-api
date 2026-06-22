package com.nexus.nexusiam.presentation.dto.response;

import java.util.UUID;

public record RegisterResponse(
        UUID organizationId,
        String organizationName,
        String organizationSlug,
        UUID ownerId,
        String ownerEmail,
        String accessToken,
        String refreshToken,
        long accessTokenExpiresIn,
        long refreshTokenExpiresIn
) {}
