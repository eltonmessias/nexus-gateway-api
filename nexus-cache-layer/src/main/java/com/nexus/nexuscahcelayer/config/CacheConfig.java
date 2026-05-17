package com.nexus.nexuscahcelayer.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
@ConfigurationProperties(prefix = "nexus.cache")
public class CacheConfig {


    private Duration defaultTtl = Duration.ofMinutes(5);
    private Map<String, NamespaceConfig> namespaces = new HashMap<>();

    public Duration getTtlForNamespace(String namespace) {
        return namespaces.getOrDefault(namespace, new NamespaceConfig())
                .getTtl() != null
                ? namespaces.get(namespace).getTtl()
                : defaultTtl;
    }

    public Duration getDefaultTtl() {return defaultTtl;}
    public void setDefaultTtl(Duration defaultTtl) { this.defaultTtl = defaultTtl; }
    public Map<String, NamespaceConfig> getNamespaces() {return namespaces;}
    public void setNamespaces(Map<String, NamespaceConfig> n) {this.namespaces = n;}

    public static class NamespaceConfig {
        private Duration ttl;
        public Duration getTtl() {return ttl;}
        public void setTtl(Duration ttl) {this.ttl = ttl;}
    }
}
