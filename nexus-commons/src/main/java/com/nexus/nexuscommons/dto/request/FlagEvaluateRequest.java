package com.nexus.nexuscommons.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record FlagEvaluateRequest(
        @NotBlank(message = "Flag key is required")
        String flagKey,

        String userId,

        Map<String, String> context
) {
}
