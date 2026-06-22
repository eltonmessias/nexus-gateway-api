package com.nexus.nexusiam.presentation.dto.response;

import java.util.UUID;

public record ApiClientCreatedResponse(
        UUID id,
        String name,
        UUID projectId,
        UUID organizationId,
        String clientId,
        String apiKey,
        Integer rateLimitRpm,
        Integer rateLimitBurst
) {
}
