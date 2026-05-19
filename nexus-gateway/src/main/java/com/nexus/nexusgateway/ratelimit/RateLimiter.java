package com.nexus.nexusgateway.ratelimit;

import reactor.core.publisher.Mono;

public interface RateLimiter {
    Mono<RateLimitResult> isAllowed(String clientId, int limitRpm);
}
