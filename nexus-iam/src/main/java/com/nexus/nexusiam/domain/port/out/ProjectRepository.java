package com.nexus.nexusiam.domain.port.out;

import com.nexus.nexusiam.domain.model.Project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository {
    Project save(Project project);
    Optional<Project> findById(UUID id);
    List<Project> findByOrganizationId(UUID organizationId);
    List<Project> findByOrganizationId(UUID organizationId, int page, int size);
    long countByOrganizationId(UUID organizationId);
    List<Project> findByTeamId(UUID teamId, int page, int size);
    long countByTeamId(UUID teamId);
    Optional<Project> findByName(String name);
    List<Project> findAll();
    List<Project> findAll(int page, int size);
    long count();
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
