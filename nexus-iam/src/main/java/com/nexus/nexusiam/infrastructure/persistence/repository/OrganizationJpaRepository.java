package com.nexus.nexusiam.infrastructure.persistence.repository;

import com.nexus.nexusiam.infrastructure.persistence.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationJpaRepository extends JpaRepository<OrganizationEntity, UUID> {
    Optional<OrganizationEntity> findByName(String name);
    Optional<OrganizationEntity> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
