package com.nexus.nexuscahcelayer.strategy;

import com.nexus.nexuscahcelayer.service.CacheService;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class WriteThroughStrategy {

    private final CacheService cacheService;

    public WriteThroughStrategy(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    public <T> void execute(String namespace, String key, T value, Consumer<T> dbWriter) {
        // persiste na fonte de dados
        dbWriter.accept(value);
        // Actualiza p cache imediatamente
        cacheService.put(namespace, key, value);
    }
}
