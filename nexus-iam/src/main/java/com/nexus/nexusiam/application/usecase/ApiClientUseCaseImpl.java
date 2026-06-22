package com.nexus.nexusiam.application.usecase;


import com.nexus.nexusiam.domain.model.ApiClient;
import com.nexus.nexusiam.domain.port.in.ApiClientUseCase;
import com.nexus.nexusiam.domain.service.ApiClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ApiClientUseCaseImpl implements ApiClientUseCase {

    private final ApiClientService apiClientService;

    @Override
    public ApiClient create(ApiClient apiClient) {
        return apiClientService.create(apiClient);
    }

    @Override
    public ApiClient findById(UUID id) {
        return apiClientService.findById(id);
    }

    @Override
    public ApiClient findByClientId(String clientId) {
        return apiClientService.findClientById(clientId);
    }

    @Override
    public List<ApiClient> findAllByOrganizationId(UUID organizationId) {
        return apiClientService.findAllByOrganizationId(organizationId);
    }

    @Override
    public List<ApiClient> findAllByProjectId(UUID projectId) {
        return apiClientService.findAllByProjectId(projectId);
    }

    @Override
    public ApiClient update(UUID id, ApiClient apiClient) {
        return apiClientService.update(id, apiClient);
    }

    @Override
    public void delete(UUID id) {
        apiClientService.delete(id);
    }

    @Override
    public void deactivate(UUID id) {
        apiClientService.deactivate(id);
    }
}
