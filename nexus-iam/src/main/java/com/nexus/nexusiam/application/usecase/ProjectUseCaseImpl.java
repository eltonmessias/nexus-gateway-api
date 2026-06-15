package com.nexus.nexusiam.application.usecase;

import com.nexus.nexusiam.domain.model.Project;
import com.nexus.nexusiam.domain.port.in.ProjectUseCase;
import com.nexus.nexusiam.domain.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectUseCaseImpl implements ProjectUseCase {
    private final ProjectService projectService;

    @Override
    public Project create(Project project) {
        Instant now = Instant.now();
        return projectService.create(Project.builder()
                .name(project.getName())
                .description(project.getDescription())
                .teamId(project.getTeamId())
                .createdAt(now)
                .updatedAt(now)
                .active(true)
                .build());
    }

    @Override
    public Project update(UUID id, Project project) {
        return projectService.update(id, Project.builder()
                .id(id)
                .name(project.getName())
                .description(project.getDescription())
                .teamId(project.getTeamId())
                .updatedAt(Instant.now())
                .active(project.isActive())
                .build());
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
