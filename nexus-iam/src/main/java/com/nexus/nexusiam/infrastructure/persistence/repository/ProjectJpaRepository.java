package com.nexus.nexusiam.infrastructure.persistence.repository;

import com.nexus.nexusiam.infrastructure.persistence.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectJpaRepository extends JpaRepository<ProjectEntity, UUID> {
    List<ProjectEntity> findAllByOrganizationId(UUID organizationId);
    org.springframework.data.domain.Page<ProjectEntity> findAllByOrganizationId(UUID organizationId, org.springframework.data.domain.Pageable pageable);
    long countByOrganizationId(UUID organizationId);
    Optional<ProjectEntity> findByName(String name);
    org.springframework.data.domain.Page<ProjectEntity> findAllByTeamId(UUID teamId, org.springframework.data.domain.Pageable pageable);
    long countByTeamId(UUID teamId);
}
