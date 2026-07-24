package com.nexus.nexusiam.domain.port.in;

import com.nexus.nexuscommons.dto.response.PagedResult;
import com.nexus.nexusiam.domain.model.ApiClient;

import java.util.List;
import java.util.UUID;

public interface ApiClientUseCase {
    ApiClient create(ApiClient apiClient);
    ApiClient findById(UUID id);
    ApiClient findByClientId(String clientId);
    List<ApiClient> findAllByOrganizationId(UUID organizationId);
    List<ApiClient> findAllByProjectId(UUID projectId);
    PagedResult<ApiClient> findAll(int page, int size);
    ApiClient update(UUID id, ApiClient apiClient);
    void delete(UUID id);
    void deactivate(UUID id);
    String rotateApiKey(UUID id);
}
