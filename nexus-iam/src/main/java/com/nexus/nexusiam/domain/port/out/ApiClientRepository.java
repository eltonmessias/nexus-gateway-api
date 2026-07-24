package com.nexus.nexusiam.domain.port.out;

import com.nexus.nexusiam.domain.model.ApiClient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiClientRepository {
    ApiClient save(ApiClient apiClient);
    Optional<ApiClient> findById(UUID id);
    Optional<ApiClient> findByClientId(String clientId);
    List<ApiClient> findAllByOrganizationId(UUID organizationId);
    List<ApiClient> findAllByProjectId(UUID projectId);
    List<ApiClient> findAll(int page, int size);
    long count();
    void deleteById(UUID id);
    boolean existsByClientId(String clientId);
}
