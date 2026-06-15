package com.nexus.nexusiam.infrastructure.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "nexus.security.jwt")
public class JwtProperties {
    private String secret;
    private long expiration;
}
