package com.nexus.nexuscommons.dto.request;

import jakarta.validation.constraints.NotBlank;

public record FlagEvaluateRequest(
        @NotBlank
        String flagKey,

        @NotBlank
        String userId,

       @NotBlank
       String environment
) {
}
