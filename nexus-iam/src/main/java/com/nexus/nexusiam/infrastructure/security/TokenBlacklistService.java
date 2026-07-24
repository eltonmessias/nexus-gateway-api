package com.nexus.nexusiam.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private static final String BLACKLIST_PREFIX = "nexus:token:blacklist:";

    private final StringRedisTemplate redisTemplate;
    private final JwtService jwtService;

    public void revoke(String token) {
        try {
            Date expiration = jwtService.extractExpiration(token);
            long ttlMs = expiration.getTime() - System.currentTimeMillis();
            if (ttlMs > 0) {
                redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "1", Duration.ofMillis(ttlMs));
            }
        } catch (Exception e) {
            // Revocation store unavailable — the token will still expire on its own.
            log.warn("Token revocation store unavailable; token will expire naturally: {}", e.getMessage());
        }
    }

    public boolean isRevoked(String token) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
        } catch (Exception e) {
            // Fail-open: a blacklist outage must not take down all authentication.
            // Valid tokens keep working; a revoked token would only slip through
            // during the (short) window the store is unreachable.
            log.warn("Token blacklist unavailable; treating token as not revoked: {}", e.getMessage());
            return false;
        }
    }
}
