package com.nexus.nexusfeatureflags.repository;

import com.nexus.nexusfeatureflags.model.Flag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FlagRepository extends JpaRepository<Flag, UUID> {
    Optional<Flag> findByKey(String key);
    List<Flag> findAllByProjectId(UUID projectId);
    Page<Flag> findAllByProjectId(UUID projectId, Pageable pageable);
    Page<Flag> findAll(Pageable pageable);

    @Query("SELECT f FROM Flag f WHERE f.projectId IN " +
           "(SELECT p.id FROM com.nexus.nexusiam.infrastructure.persistence.entity.ProjectEntity p WHERE p.organizationId = :organizationId)")
    Page<Flag> findAllByOrganizationId(@Param("organizationId") UUID organizationId, Pageable pageable);

    @Query("SELECT COUNT(f) FROM Flag f WHERE f.projectId IN " +
           "(SELECT p.id FROM com.nexus.nexusiam.infrastructure.persistence.entity.ProjectEntity p WHERE p.organizationId = :organizationId) AND f.enabled = true")
    long countEnabledByOrganizationId(@Param("organizationId") UUID organizationId);
}
