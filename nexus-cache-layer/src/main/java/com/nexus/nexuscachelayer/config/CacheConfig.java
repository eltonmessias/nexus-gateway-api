package com.nexus.nexuscachelayer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class CacheConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .activateDefaultTyping(
                        new com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator(),
                        ObjectMapper.DefaultTyping.NON_FINAL
                )
                .build();

        RedisSerializer<Object> jsonSerializer = new RedisSerializer<>() {

            @Override
            public byte[] serialize(@Nullable Object value) throws SerializationException {
                try {
                    return mapper.writeValueAsBytes(value);
                } catch (Exception e) {
                    throw new SerializationException("Failed to serialize", e);
                }
            }

            @Override
            public @Nullable Object deserialize(byte @Nullable [] bytes) throws SerializationException {
                if (bytes == null) return null;
                try {
                    return mapper.readValue(bytes, Object.class);
                } catch (Exception e) {
                    throw new SerializationException("Failed to deserialize", e);
                }
            }
        };

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jsonSerializer);
        return template;
    }

}
