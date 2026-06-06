package com.nexus.nexuscommons.dto.response;

import java.util.List;
import java.util.Map;

public record SearchResponse(
        List<Map<String, Object>> documents,
        long totalResults,
        int page,
        int size
) {
}
