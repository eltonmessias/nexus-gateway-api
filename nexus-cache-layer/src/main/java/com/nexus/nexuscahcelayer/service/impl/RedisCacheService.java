package com.nexus.nexuscahcelayer.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.nexuscahcelayer.config.CacheConfig;
import com.nexus.nexuscahcelayer.metrics.CacheMetrics;
import com.nexus.nexuscahcelayer.service.CacheService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

@Service
public class RedisCacheService implements CacheService {
    private static final Logger log = LoggerFactory.getLogger(RedisCacheService.class);
    private static final String KEY_SEP = ":";
    private static final String TAG_PREFIX = "cache:tag:";

    private final RedisTemplate<String, Object> redis;
    private final CacheConfig config;
    private final CacheMetrics metrics;
    private ObjectMapper mapper;

    public RedisCacheService(RedisTemplate<String, Object> redis,
                             CacheConfig config,
                             CacheMetrics metrics,
                             ObjectMapper mapper) {
        this.redis = redis;
        this.config = config;
        this.metrics = metrics;
        this.mapper = mapper;
    }

    @Override
    @CircuitBreaker(name = "redis-cb", fallbackMethod = "getOrLoadFallback")
    public <T> T getOrLoad(String namespace, String key, Class<T> type, Supplier<T> loader) {
        String redisKey = buildkey(namespace, key);
        Object cached = redis.opsForValue().get(redisKey);

        if (cached != null) {
            metrics.recordHit(namespace);
            log.debug("Cache HIT - key={}", redisKey);
            return mapper.convertValue(cached, type);
        }

        metrics.recordMiss(namespace);
        log.debug("Cache MISS - key={}", redisKey);

        T value = loader.get();
        if (value != null) {
            Duration ttl = config.getTtlForNamespace(namespace);
            redis.opsForValue().set(redisKey, value, ttl);
        }
        return value;
    }

    public <T> T getOrLoadFallback(String namespace, String key,
                                   Class<T> type, Supplier<T> loader,
                                   Throwable ex) {
        log.warn("Redis circuit breaker open - namespace={}, key={}, cause={}", namespace, key, ex.getMessage());
        metrics.recordFallback(namespace);
        return loader.get();
    }

    @Override
    @CircuitBreaker(name = "redis-cb", fallbackMethod = "getFallback")
    public <T> Optional<T> get(String namespace, String key, Class<T> type) {
        String redisKey = buildkey(namespace, key);
        Object value = redis.opsForValue().get(redisKey);
        if (value == null) {
            metrics.recordMiss(namespace);
            return Optional.empty();
        }
        metrics.recordHit(namespace);
        return Optional.of(mapper.convertValue(value, type));
    }

    public <T> Optional<T> getFallback(String namespace, String key,
                                       Class<T> type, Throwable ex) {
        log.warn("Redis unavailable for GET - namespace={}, key={}", namespace, key);
        return Optional.empty();
    }

    @Override
    @CircuitBreaker(name = "redis-cb")
    public void put(String namespace, String key, Object value) {
        put(namespace, key, value, config.getTtlForNamespace(namespace));
    }

    @Override
    @CircuitBreaker(name = "redis-cb")
    public void put(String namespace, String key, Object value, Duration ttl) {
        String redisKey = buildkey(namespace, key);
        redis.opsForValue().set(redisKey, value, ttl);
        log.debug("Cache PUT - key={}, ttl={}", redisKey, ttl);
    }

    @Override
    @CircuitBreaker(name = "redis-cb")
    public void evict(String namespace, String key) {
        String redisKey = buildkey(namespace, key);
        redis.delete(redisKey);
        log.debug("Cache EVICT - key={}", redisKey);
    }

    @Override
    @CircuitBreaker(name = "redis-cb")
    public void evictNamespace(String namespace) {
        String pattern = "cache:"+ namespace + KEY_SEP + "*";
        Set<String> keys = redis.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redis.delete(keys);
            log.info("Cache EVICT namespace={}, keys={}", namespace, keys.size());
        }
    }

    @Override
    @CircuitBreaker(name = "redis-cb")
    public void tag(String tag, String namespace, String key) {
        String tagKey = TAG_PREFIX + tag;
        String redisKey = buildkey(namespace, key);
        redis.opsForSet().add(tagKey, redisKey);

    }

    @Override
    @CircuitBreaker(name = "redis-cp")
    public void evictByTag(String tag) {
        String tagkey = TAG_PREFIX + tag;
        Set<Object> keys = redis.opsForSet().members(tagkey);
        if (keys != null && !keys.isEmpty()) {
            keys.forEach(k -> redis.delete(k.toString()));
            redis.delete(tagkey);
            log.info("Cache EVICT by tag={}, keys={}", tagkey, keys.size());
        }
    }

    @Override
    @CircuitBreaker(name = "redis-cp", fallbackMethod = "existsFallback")
    public boolean exists(String namespace, String key) {
        return Boolean.TRUE.equals(redis.hasKey(buildkey(namespace, key)));
    }

    public boolean existsFallback(String namespace, String key, Throwable ex) {
        return false;
    }


    private String buildkey(String namespace, String key) {
        return "cache:" + namespace + KEY_SEP + key;
    }
}
