package com.nexus.nexusgateway.domain;


import com.vladmihalcea.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(schema = "nexus_gateway", name = "api_clients")
public class ApiClient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "api_key_hash", nullable = false)
    private String apiKeyHash;

    @Column(name = "rate_limit_rpm", nullable = false)
    private int rateLimitRpm = 100;

    @Column(name = "rate_limit_burst", nullable = false)
    private int rateLimitBurst = 20;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Type(ListArrayType.class)
    @Column(name = "allowed_paths", columnDefinition = "text[]")
    private List<String> allowedPaths;

    @Column(columnDefinition = "jsonb")
    private String metadata;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }


    public UUID getId()                        { return id; }
    public String getName()                    { return name; }
    public void setName(String name)           { this.name = name; }
    public String getApiKeyHash()              { return apiKeyHash; }
    public void setApiKeyHash(String h)        { this.apiKeyHash = h; }
    public int getRateLimitRpm()               { return rateLimitRpm; }
    public void setRateLimitRpm(int r)         { this.rateLimitRpm = r; }
    public int getRateLimitBurst()             { return rateLimitBurst; }
    public void setRateLimitBurst(int b)       { this.rateLimitBurst = b; }
    public boolean isActive()                  { return active; }
    public void setActive(boolean active)      { this.active = active; }
    public List<String> getAllowedPaths()       { return allowedPaths; }
    public void setAllowedPaths(List<String> p){ this.allowedPaths = p; }
    public Instant getCreatedAt()              { return createdAt; }
    public Instant getUpdatedAt()              { return updatedAt; }

}
