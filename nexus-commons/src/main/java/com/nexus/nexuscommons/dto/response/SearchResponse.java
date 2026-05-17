package com.nexus.nexuscommons.dto.response;

import java.util.List;
import java.util.Map;

public record SearchResponse(
        List<SearchHit> hits,
        long totalHits,
        int page,
        int size,
        int totalPages,
        long durationMs,
        Map<String, Map<String, Long>> facets
) {
    public record SearchHit(
            String id,
            String externalId,
            String title,
            String excerpt,
            String url,
            double score,
            Map<String, Object> fields
    ) {}
}
