package com.nexus.nexusiam.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "api_clients")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ApiClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private UUID projectId;

    @Column(nullable = false)
    private UUID organizationId;

    @Column(nullable = false, unique = true)
    private String clientId;

    @Column(nullable = false)
    private String apiKeyHash;

    @Column(nullable = false)
    private Integer rateLimitRpm;

    @Column(nullable = false)
    private Integer rateLimitBurst;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;
}
