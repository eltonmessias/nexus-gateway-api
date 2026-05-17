package com.nexus.nexuscommons.dto.response;

public record FlagEvaluateResponse(
        String flagKey,
        Object value,
        String reason, // TARGETING | ROLLOUT | DEFAULT | OVERRIDE
        boolean enabled
) {
}
