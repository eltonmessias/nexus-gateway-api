package com.nexus.nexusiam.infrastructure.persistence.adapter;

import com.nexus.nexusiam.domain.model.Team;
import com.nexus.nexusiam.domain.port.out.TeamRepository;
import com.nexus.nexusiam.infrastructure.mapper.TeamMapper;
import com.nexus.nexusiam.infrastructure.persistence.repository.TeamJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TeamRepositoryAdapter implements TeamRepository {

    private final TeamJpaRepository jpaRepository;
    private final TeamMapper mapper;

    @Override
    public Team save(Team team) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(team)));
    }

    @Override
    public Optional<Team> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Team> findByOrganizationId(UUID organizationId) {
        return jpaRepository.findAllByOrganizationId(organizationId).stream().map(mapper::toDomain).toList();
    }


    @Override
    public List<Team> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Team> findAll(int page, int size) {
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
