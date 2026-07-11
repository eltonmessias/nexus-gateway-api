package com.nexus.nexusgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@ComponentScan(basePackages = {
        "com.nexus.nexusgateway",
        "com.nexus.nexusfeatureflags",
        "com.nexus.nexusjobqueue",
        "com.nexus.nexussearchengine",
        "com.nexus.nexuscachelayer",
        "com.nexus.nexusiam"
})
@EnableJpaRepositories(basePackages = {
        "com.nexus.nexusfeatureflags.repository",
        "com.nexus.nexusjobqueue.repository",
        "com.nexus.nexusiam.infrastructure.persistence.repository",
        "com.nexus.nexussearchengine.repository"
})
@EntityScan(basePackages = {
        "com.nexus.nexusfeatureflags.model",
        "com.nexus.nexusjobqueue.model",
        "com.nexus.nexusiam.infrastructure.persistence.entity",
        "com.nexus.nexussearchengine.model"
})
public class NexusGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(NexusGatewayApplication.class, args);
    }
}