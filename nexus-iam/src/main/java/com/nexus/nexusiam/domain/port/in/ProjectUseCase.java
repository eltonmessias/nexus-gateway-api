package com.nexus.nexusiam.domain.port.in;

import com.nexus.nexuscommons.dto.response.PagedResult;
import com.nexus.nexusiam.domain.model.Project;

import java.util.UUID;

public interface ProjectUseCase {
    Project create(Project project);
    Project update(UUID id, Project project);
    Project findById(UUID id);
    Project findByName(String name);
    PagedResult<Project> findAll(int page, int size);
    PagedResult<Project> findByOrganizationId(UUID organizationId, int page, int size);
    PagedResult<Project> findByTeamId(UUID teamId, int page, int size);
    void delete(UUID id);
}
