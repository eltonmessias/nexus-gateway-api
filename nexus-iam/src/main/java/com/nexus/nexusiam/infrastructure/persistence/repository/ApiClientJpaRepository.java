package com.nexus.nexusiam.infrastructure.persistence.repository;

import com.nexus.nexusiam.infrastructure.persistence.entity.ApiClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiClientJpaRepository extends JpaRepository<ApiClientEntity, UUID> {
    Optional<ApiClientEntity> findByClientId(String clientId);
    List<ApiClientEntity> findAllByOrganizationId(UUID organizationId);
    List<ApiClientEntity> findAllByProjectId(UUID projectId);
    boolean existsByClientId(String clientId);
}
