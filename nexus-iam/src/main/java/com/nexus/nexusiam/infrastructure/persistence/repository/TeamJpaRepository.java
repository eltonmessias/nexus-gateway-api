package com.nexus.nexusiam.infrastructure.persistence.repository;

import com.nexus.nexusiam.infrastructure.persistence.entity.TeamEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TeamJpaRepository extends JpaRepository<TeamEntity, UUID> {
    List<TeamEntity> findAllByOrganizationId(UUID organizationId);
    Page<TeamEntity> findAllByOrganizationId(UUID organizationId, Pageable pageable);
    long countByOrganizationId(UUID organizationId);
}
