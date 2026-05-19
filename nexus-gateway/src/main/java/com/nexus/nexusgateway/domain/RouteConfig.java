package com.nexus.nexusgateway.domain;


import com.vladmihalcea.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(schema = "nexus_gateway", name = "route_configs")
public class RouteConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "route_id", nullable = false, unique = true)
    private String routeId;

    @Column(nullable = false)
    private String uri;

    @Column(name = "path_predicate", nullable = false)
    private String pathPredicate;

    @Type(ListArrayType.class)
    @Column(name = "method_predicates", columnDefinition = "varchar[]")
    private List<String> methodPredicates;

    @Column(name = "rate_limit_rpm")
    private Integer rateLimitRpm;

    @Column(name = "timeout_ms", nullable = false)
    private int timeoutMs = 5000;

    @Column(name = "retry_attempts", nullable = false)
    private int retryAttempts = 2;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    // getters e setters
    public UUID getId()                        { return id; }
    public String getRouteId()                 { return routeId; }
    public void setRouteId(String routeId)     { this.routeId = routeId; }
    public String getUri()                     { return uri; }
    public void setUri(String uri)             { this.uri = uri; }
    public String getPathPredicate()           { return pathPredicate; }
    public void setPathPredicate(String p)     { this.pathPredicate = p; }
    public List<String> getMethodPredicates()  { return methodPredicates; }
    public void setMethodPredicates(List<String> m) { this.methodPredicates = m; }
    public Integer getRateLimitRpm()           { return rateLimitRpm; }
    public void setRateLimitRpm(Integer r)     { this.rateLimitRpm = r; }
    public int getTimeoutMs()                  { return timeoutMs; }
    public void setTimeoutMs(int t)            { this.timeoutMs = t; }
    public int getRetryAttempts()              { return retryAttempts; }
    public void setRetryAttempts(int r)        { this.retryAttempts = r; }
    public boolean isActive()                  { return active; }
    public void setActive(boolean active)      { this.active = active; }
    public Instant getCreatedAt()              { return createdAt; }
}
