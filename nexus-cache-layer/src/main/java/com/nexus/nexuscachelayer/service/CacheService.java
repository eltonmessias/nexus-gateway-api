package com.nexus.nexuscachelayer.service;

import java.time.Duration;
import java.util.Optional;

public interface CacheService {

    void set(String key, Object value);
    void set(String key, Object value, Duration ttl);
    Optional<Object> get(String key);
    void delete(String key);
    boolean exists(String key);
}
