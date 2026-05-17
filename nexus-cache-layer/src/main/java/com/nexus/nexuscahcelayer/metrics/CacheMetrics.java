package com.nexus.nexuscahcelayer.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class CacheMetrics {
    private final MeterRegistry registry;
    private final ConcurrentHashMap<String, Counter> hitCounters      = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Counter> missCounters     = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Counter> fallbackCounters = new ConcurrentHashMap<>();

    public CacheMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    public void recordHit(String namespace) {
        hitCounters.computeIfAbsent(namespace, ns ->
                Counter.builder("nexus.cache.hits")
                        .tag("namespace", ns)
                        .description("Cache hit count")
                        .register(registry)
        ).increment();
    }

    public void recordMiss(String namespace) {
        missCounters.computeIfAbsent(namespace, ns ->
                Counter.builder("nexus.cache.misses")
                        .tag("namespace", ns)
                        .description("Cache miss count")
                        .register(registry)
        ).increment();
    }

    public void recordFallback(String namespace) {
        fallbackCounters.computeIfAbsent(namespace, ns ->
                Counter.builder("nexus.cache.fallbacks")
                        .tag("namespace", ns)
                        .description("Cache fallback count (Redis unavailable)")
                        .register(registry)
        ).increment();
    }

}
