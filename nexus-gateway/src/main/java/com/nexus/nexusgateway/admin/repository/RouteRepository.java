package com.nexus.nexusgateway.admin.repository;

import com.nexus.nexusgateway.domain.RouteConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RouteRepository extends JpaRepository<RouteConfig, UUID> {
    List<RouteConfig> findByActiveTrue();
    Optional<RouteConfig> findByRouteId(String routeId);
}
