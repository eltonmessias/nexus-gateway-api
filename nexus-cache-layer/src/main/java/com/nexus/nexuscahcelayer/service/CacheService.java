package com.nexus.nexuscahcelayer.service;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

public interface CacheService {

    /**
     * Cache-aside: devolve do cache; em miss executa o supplier e popula.
     */
    <T> T getOrLoad(String namespace, String key, Class<T> type, Supplier<T> loader);

    /**
     * Leitura directa do cache
     */
    <T> Optional<T> get(String namespace, String key, Class<T> type);

    /**
    * Escrita directa com TTl do namespace
    */
    void put(String namespace, String key,  Object value);

    /**
     * Escrita com TTL customizado
     */
    void put(String namespace, String key, Object value, Duration ttl);

    /**
     * Invalidar uma chave especifica
     */
    void evict(String namespace, String key);

    /**
     * Invalidar todas as chaves de um namespace
     */
    void evictNamespace(String namespace);

    /**
     * Associar chaves a uma tag para invalidacao em grupo
     */
    void tag(String tag, String namespace, String key);

    /**
     * Invalidar todas as chaves associadas a uuma tag
     */
    void evictByTag(String tag);

    /**
     * Verificar se uma chave4 existe no cache
     */
    boolean exists(String namespace, String key);


}
