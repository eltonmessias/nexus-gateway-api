package com.nexus.nexusgateway.ratelimit;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@Component
public class SlidingWindowRateLimiter implements RateLimiter {

    private static final Logger log = LoggerFactory.getLogger(SlidingWindowRateLimiter.class);

    private final ReactiveRedisTemplate<String, String> redis;
    private final RedisScript<List<Long>> rateLimitScript;

    public SlidingWindowRateLimiter(ReactiveRedisTemplate<String, String> redis) {
        this.redis = redis;
        this.rateLimitScript = RedisScript.of(
                new ClassPathResource("scripts/rate_limiter.lua"),
                (Class<List<Long>>) (Class<?>) List.class
        );
    }

    @Override
    public Mono<RateLimitResult> isAllowed(String clientId, int limitRpm) {
        String key    = "rl:" + clientId;
        long now      = Instant.now().toEpochMilli();
        long window   = 60_000L; // 60 segundos

        return redis.execute(
                        rateLimitScript,
                        List.of(key),
                        List.of(String.valueOf(now),
                                String.valueOf(window),
                                String.valueOf(limitRpm))
                )
                .next()
                .map(result -> {
                    boolean allowed   = result.get(0) == 1L;
                    long remaining    = result.get(1);
                    return new RateLimitResult(allowed, remaining, limitRpm);
                })
                .doOnNext(r -> {
                    if (!r.allowed()) {
                        log.warn("Rate limit exceeded — clientId={}, limit={}", clientId, limitRpm);
                    }
                });
    }
}
