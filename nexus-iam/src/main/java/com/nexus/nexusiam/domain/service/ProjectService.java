package com.nexus.nexusiam.domain.service;

import com.nexus.nexusiam.domain.exception.ProjectNotFoundException;
import com.nexus.nexusiam.domain.exception.TeamNotFoundException;
import com.nexus.nexusiam.domain.model.Project;
import com.nexus.nexusiam.domain.port.in.ProjectUseCase;
import com.nexus.nexusiam.domain.port.out.ProjectRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    public Project create(Project project) {
        return projectRepository.save(project);
    }

    public Project update(UUID id, Project project) {
        projectRepository.findById(id).orElseThrow(() -> new TeamNotFoundException(id));
        return projectRepository.save(project);
    }

    public Project findById(UUID id) {
        return projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException(id));
    }

    public Project findByName(String name) {
        return projectRepository.findByName(name).orElseThrow(() -> new ProjectNotFoundException(name));
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public void delete(UUID id) {
        projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException(id));
        projectRepository.deleteById(id);
    }
}
