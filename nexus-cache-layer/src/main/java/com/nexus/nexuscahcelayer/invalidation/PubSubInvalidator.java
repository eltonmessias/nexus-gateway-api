package com.nexus.nexuscahcelayer.invalidation;


import com.nexus.nexuscahcelayer.service.CacheService;
import com.nexus.nexuscommons.event.CacheInvalidatedEvent;
import com.nexus.nexuscommons.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;


/**
 * Publica e consome eventos de invalidação via Redis pub/sub.
 * Garante que todas as instâncias do serviço invalidam o cache
 * quando uma mudança ocorre — crítico em ambientes com múltiplas réplicas.
 */

@Component
public class PubSubInvalidator implements MessageListener {
    private static final Logger log = LoggerFactory.getLogger(PubSubInvalidator.class);
    private static final String CHANNEL = "nexus:cache:invalidation";

    private final StringRedisTemplate redis;
    private final CacheService cacheService;

    public PubSubInvalidator(StringRedisTemplate redis, CacheService cacheService, RedisMessageListenerContainer container) {
        this.redis = redis;
        this.cacheService = cacheService;
        container.addMessageListener(this, new PatternTopic(CHANNEL));
    }

    public void publish(CacheInvalidatedEvent event) {
        redis.convertAndSend(CHANNEL, JsonUtil.toJson(event));
        log.debug("Cache invalidation published — namespace={}, keys={}",
                event.namespace(), event.keys().size());
    }
    @Override
    public void onMessage(Message message, byte[] pattern) {
        CacheInvalidatedEvent event = JsonUtil.fromJson(
                new String(message.getBody()), CacheInvalidatedEvent.class
        );
        event.keys().forEach(key -> cacheService.evict(event.namespace(), key));

        log.debug("Cache invalidation received — namespace={}, keys={}",
                event.namespace(), event.keys().size());
    }
}
