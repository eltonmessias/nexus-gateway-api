package com.nexus.nexuscommons.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record SearchRequest(
        @NotBlank
        String query,

        @NotBlank
        String indexName,

        @Min(0)
        int page,

        @Min(1) @Max(100)
        int size
) {
}
