package com.nexus.nexuscommons.event;

import java.time.Instant;

public record CacheInvalidatedEvent(
        String cacheKey,
        String region,
        Instant invalidatedAt
) {
}
