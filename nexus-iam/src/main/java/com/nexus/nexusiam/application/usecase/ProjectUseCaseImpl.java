package com.nexus.nexusiam.application.usecase;

import com.nexus.nexusiam.domain.model.Project;
import com.nexus.nexusiam.domain.port.in.ProjectUseCase;
import com.nexus.nexusiam.domain.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectUseCaseImpl implements ProjectUseCase {
    private final ProjectService projectService;

    @Override
    public Project create(Project project) {
        return projectService.create(project);
    }

    @Override
    public Project update(UUID id, Project project) {
        return projectService.update(id, project);
    }

    @Override
    public Project findById(UUID id) {
        return projectService.findById(id);
    }

    @Override
    public Project findByName(String name) {
        return projectService.findByName(name);
    }

    @Override
    public List<Project> findAll() {
        return projectService.findAll();
    }

    @Override
    public void delete(UUID id) {
        projectService.delete(id);
    }
}
