package com.nexus.nexuscommons.dto.response;

public record FlagEvaluateResponse(
        String key,
        boolean enabled,
        String reason
) {
}
