package com.nexus.nexusiam.infrastructure.persistence.adapter;

import com.nexus.nexusiam.domain.model.Project;
import com.nexus.nexusiam.domain.port.out.ProjectRepository;
import com.nexus.nexusiam.infrastructure.mapper.ProjectMapper;
import com.nexus.nexusiam.infrastructure.persistence.repository.ProjectJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProjectRepositoryAdapter implements ProjectRepository {

    private final ProjectJpaRepository jpaRepository;
    private final ProjectMapper mapper;

    @Override
    public Project save(Project project) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(project)));
    }

    @Override
    public Optional<Project> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Project> findByTeamId(UUID teamId) {
        return jpaRepository.findAllByTeamId(teamId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Project> findByName(String name) {
        return jpaRepository.findByName(name).map(mapper::toDomain);
    }


    @Override
    public List<Project> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Project> findAll(int page, int size) {
        return jpaRepository.findAll(PageRequest.of(page, size)).stream().map(mapper::toDomain).toList();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
}
