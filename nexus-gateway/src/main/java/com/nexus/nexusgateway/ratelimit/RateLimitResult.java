package com.nexus.nexusgateway.ratelimit;

public record RateLimitResult(
        boolean allowed,
        int currentCount,
        int limit,
        long retryAfterMs
) {}
