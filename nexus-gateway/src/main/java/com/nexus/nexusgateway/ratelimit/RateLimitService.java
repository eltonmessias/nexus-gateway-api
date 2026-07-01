package com.nexus.nexusgateway.ratelimit;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final StringRedisTemplate redisTemplate;
    private DefaultRedisScript<List> rateLimitScript;

    @PostConstruct
    public void init() {
        rateLimitScript = new DefaultRedisScript<>();
        rateLimitScript.setScriptSource(new ResourceScriptSource(
                new ClassPathResource("scripts/sliding_window_rate_limit.lua")));
        rateLimitScript.setResultType(List.class);
    }

    public RateLimitResult check(String clientId, int limitRpm, int burstLimit) {
        String key = "rl:" + clientId;
        long now = System.currentTimeMillis();
        long windowMs = 60_000L;

        @SuppressWarnings("unchecked")
        List<Long> result = (List<Long>) redisTemplate.execute(
                rateLimitScript,
                List.of(key),
                String.valueOf(windowMs),
                String.valueOf(limitRpm),
                String.valueOf(now)
        );

        boolean allowed = result != null && result.get(0) == 1L;
        long currentCount = result != null ? result.get(1) : 0L;
        long retryAfterMs = result != null ? result.get(2) : windowMs;

        return new RateLimitResult(allowed, (int) currentCount, limitRpm, retryAfterMs);
    }
}
