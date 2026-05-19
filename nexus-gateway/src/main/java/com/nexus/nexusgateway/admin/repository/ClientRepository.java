package com.nexus.nexusgateway.admin.repository;

import com.nexus.nexusgateway.domain.ApiClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<ApiClient, UUID> {
    Optional<ApiClient> findByApiKeyHash(String apiKeyHash);
    Optional<ApiClient> findByNameAndActiveTrue(String name);
    boolean existsByName(String name);
}
