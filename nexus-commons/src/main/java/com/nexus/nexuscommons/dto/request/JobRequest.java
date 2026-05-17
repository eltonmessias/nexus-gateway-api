package com.nexus.nexuscommons.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JobRequest(
        @NotBlank(message = "Job type is required")
        String type,

        @NotNull(message = "Payload is required")
        Object payload,

        @Min(1) @Max(10)
        int priority,

        String idempotencyKey

) {
    public JobRequest {
        if (priority < 1 || priority > 10) priority = 5;
    }
}
