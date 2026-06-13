package com.nexus.nexusiam.infrastructure.persistence.repository;

import com.nexus.nexusiam.infrastructure.persistence.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectJpaRepository extends JpaRepository<ProjectEntity, UUID> {
    List<ProjectEntity> findAllByTeamId(UUID teamId);
    Optional<ProjectEntity> findByName(String name);
}
