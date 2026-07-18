package com.nexus.nexusiam.presentation.dto.response;

import java.util.UUID;

public record ApiKeyRotatedResponse(
        UUID clientId,
        String apiKey,
        String message
) {}
