package com.nexus.nexusgateway.ratelimit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateLimitServiceTest {

    @Mock private StringRedisTemplate redisTemplate;

    private RateLimitService rateLimitService;

    @BeforeEach
    void setUp() {
        rateLimitService = new RateLimitService(redisTemplate);
        rateLimitService.init();
    }

    @Test
    void check_shouldAllowRequest_whenUnderLimit() {
        when(redisTemplate.execute(any(DefaultRedisScript.class), anyList(), anyString(), anyString(), anyString()))
                .thenReturn(List.of(1L, 1L, 0L));

        RateLimitResult result = rateLimitService.check("nexus_client1", 100, 20);

        assertThat(result.allowed()).isTrue();
        assertThat(result.currentCount()).isEqualTo(1);
    }

    @Test
    void check_shouldDenyRequest_whenOverLimit() {
        when(redisTemplate.execute(any(DefaultRedisScript.class), anyList(), anyString(), anyString(), anyString()))
                .thenReturn(List.of(0L, 100L, 30000L));

        RateLimitResult result = rateLimitService.check("nexus_client1", 100, 20);

        assertThat(result.allowed()).isFalse();
        assertThat(result.retryAfterMs()).isEqualTo(30000L);
    }

    @Test
    void check_shouldHandleNullRedisResponse() {
        when(redisTemplate.execute(any(DefaultRedisScript.class), anyList(), anyString(), anyString(), anyString()))
                .thenReturn(null);

        RateLimitResult result = rateLimitService.check("nexus_client1", 100, 20);

        assertThat(result.allowed()).isFalse();
    }
}
