package com.nexus.nexuscommons.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record JobRequest(
        @NotBlank
        String type,

        @NotNull
        Map<String, Object> payload,

        @Min(1) @Max(10)
        int priority
) {
}
