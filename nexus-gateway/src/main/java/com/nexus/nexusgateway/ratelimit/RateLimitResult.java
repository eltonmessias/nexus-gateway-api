package com.nexus.nexusgateway.ratelimit;

public record RateLimitResult(
        boolean allowed,
        long remaining,
        int limit
) {
}
