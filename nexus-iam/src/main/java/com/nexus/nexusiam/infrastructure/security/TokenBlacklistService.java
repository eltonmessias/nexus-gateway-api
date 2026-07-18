package com.nexus.nexusiam.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private static final String BLACKLIST_PREFIX = "nexus:token:blacklist:";

    private final StringRedisTemplate redisTemplate;
    private final JwtService jwtService;

    public void revoke(String token) {
        Date expiration = jwtService.extractExpiration(token);
        long ttlMs = expiration.getTime() - System.currentTimeMillis();
        if (ttlMs > 0) {
            redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "1", Duration.ofMillis(ttlMs));
        }
    }

    public boolean isRevoked(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }
}
