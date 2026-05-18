package com.nexus.nexuscahcelayer.invalidation;

import com.nexus.nexuscahcelayer.service.CacheService;
import org.springframework.stereotype.Component;

/**
 * Invalida grupos de chaves associadas a uma tag.
 * Exemplo: tag "flag:new-checkout" agrupa todas as chaves
 * de cache relacionadas com essa flag — invalida tudo de uma vez.
 */

@Component
public class TagBasedInvalidator {
    private final CacheService cacheService;

    public TagBasedInvalidator(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    public void tag(String tag, String namespace, String key) {
        cacheService.tag(namespace, key, tag);
    }

    public void invalidate(String tag) {
        cacheService.evictByTag(tag);
    }

}
