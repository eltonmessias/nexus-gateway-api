package com.nexus.nexusiam.domain.port.in;

import com.nexus.nexusiam.domain.model.Project;

import java.util.List;
import java.util.UUID;

public interface ProjectUseCase {
    Project create(Project project);
    Project update(UUID id, Project project);
    Project findById(UUID id);
    Project findByName(String name);
    List<Project> findAll();
    void delete(UUID id);

}
