package com.nexus.nexuscahcelayer.strategy;

import com.nexus.nexuscahcelayer.service.CacheService;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class CacheAsideStrategy {

    private final CacheService cacheService;

    public CacheAsideStrategy(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    public <T> T execute(String namespace, String key, Class<T> type, Supplier<T> dbLoader) {
        return cacheService.getOrLoad(namespace, key, type, dbLoader);
    }
}
