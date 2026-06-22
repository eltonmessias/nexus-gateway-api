package com.nexus.nexusiam.infrastructure.persistence.adapter;

import com.nexus.nexusiam.domain.model.ApiClient;
import com.nexus.nexusiam.domain.port.out.ApiClientRepository;
import com.nexus.nexusiam.infrastructure.mapper.ApiClientMapper;
import com.nexus.nexusiam.infrastructure.persistence.repository.ApiClientJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ApiClientRepositoryAdapter implements ApiClientRepository {

    private final ApiClientJpaRepository jpaRepository;
    private final ApiClientMapper mapper;

    @Override
    public ApiClient save(ApiClient apiClient) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(apiClient)));
    }

    @Override
    public Optional<ApiClient> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<ApiClient> findByClientId(String clientId) {
        return jpaRepository.findByClientId(clientId).map(mapper::toDomain);
    }

    @Override
    public List<ApiClient> findAllByOrganizationId(UUID organizationId) {
        return jpaRepository.findAllByOrganizationId(organizationId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ApiClient> findAllByProjectId(UUID projectId) {
        return jpaRepository.findAllByProjectId(projectId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByClientId(String clientId) {
        return jpaRepository.existsByClientId(clientId);
    }
}
