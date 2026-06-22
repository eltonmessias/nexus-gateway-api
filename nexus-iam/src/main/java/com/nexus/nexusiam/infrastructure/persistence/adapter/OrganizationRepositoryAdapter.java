package com.nexus.nexusiam.infrastructure.persistence.adapter;

import com.nexus.nexusiam.domain.model.Organization;
import com.nexus.nexusiam.domain.port.out.OrganizationRepository;
import com.nexus.nexusiam.infrastructure.mapper.OrganizationMapper;
import com.nexus.nexusiam.infrastructure.persistence.repository.OrganizationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrganizationRepositoryAdapter implements OrganizationRepository {
    private final OrganizationJpaRepository jpaRepository;
    private final OrganizationMapper mapper;

    @Override
    public Organization save(Organization organization) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(organization)));
    }

    @Override
    public Optional<Organization> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Organization> findBySlug(String slug) {
        return jpaRepository.findBySlug(slug).map(mapper::toDomain);
    }

    @Override
    public List<Organization> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return jpaRepository.existsBySlug(slug);
    }
}
