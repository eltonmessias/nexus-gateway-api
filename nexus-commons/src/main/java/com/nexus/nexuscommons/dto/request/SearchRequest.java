package com.nexus.nexuscommons.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record SearchRequest(
        @NotBlank(message = "Query is required")
        String query,

        Map<String, String> filters,

        String sortBy,

        @Min(0) int page,
        @Min(1) int size
) {
    public SearchRequest {
        if (page < 0) page = 0;
        if (size < 1) size = 20;
        if (size > 100) size = 100;
    }
}
