package com.nexus.nexuscommons.event;

import java.time.Instant;
import java.util.List;

public record CacheInvalidatedEvent(
        String namespace,
        List<String> keys,
        String reason,
        Instant invalidatedAt
) {
    public static CacheInvalidatedEvent of(String namespace, List<String> keys, String reason) {
        return new CacheInvalidatedEvent(namespace, keys, reason, Instant.now());
    }
}
