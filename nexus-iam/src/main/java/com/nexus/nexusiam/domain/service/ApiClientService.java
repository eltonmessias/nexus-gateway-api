package com.nexus.nexusiam.domain.service;

import com.nexus.nexusiam.domain.exception.ApiClientNotFoundException;
import com.nexus.nexusiam.domain.model.ApiClient;
import com.nexus.nexusiam.domain.port.out.ApiClientRepository;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ApiClientService {

    private final ApiClientRepository apiClientRepository;

    public ApiClient create(ApiClient apiClient) {
        return apiClientRepository.save(apiClient);
    }

    public ApiClient findById(UUID id) {
        return apiClientRepository.findById(id)
                .orElseThrow(() -> new ApiClientNotFoundException(id));
    }

    public ApiClient findClientById(String clientId) {
        return apiClientRepository.findByClientId(clientId)
                .orElseThrow(() -> new ApiClientNotFoundException(clientId));
    }

    public List<ApiClient> findAllByOrganizationId(UUID organizationId) {
        return apiClientRepository.findAllByOrganizationId(organizationId);
    }

    public List<ApiClient> findAllByProjectId(UUID projectId) {
        return apiClientRepository.findAllByProjectId(projectId);
    }

    public ApiClient update(UUID id, ApiClient apiClient) {
        apiClientRepository.findById(id).orElseThrow(() -> new ApiClientNotFoundException(id));
        return apiClientRepository.save(apiClient);
    }

    public void delete(UUID id) {
        apiClientRepository.findById(id).orElseThrow(() -> new ApiClientNotFoundException(id));
        apiClientRepository.deleteById(id);
    }

    public String rotateApiKey(UUID id, String newApiKeyHash) {
        ApiClient client = findById(id);
        ApiClient updated = ApiClient.builder()
                .id(client.getId())
                .name(client.getName())
                .projectId(client.getProjectId())
                .organizationId(client.getOrganizationId())
                .clientId(client.getClientId())
                .apiKeyHash(newApiKeyHash)
                .rateLimitRpm(client.getRateLimitRpm())
                .rateLimitBurst(client.getRateLimitBurst())
                .active(client.isActive())
                .createdAt(client.getCreatedAt())
                .updatedAt(Instant.now())
                .build();
        apiClientRepository.save(updated);
        return newApiKeyHash;
    }

    public void deactivate(UUID id) {
        ApiClient client = findById(id);
        ApiClient deactivated = ApiClient.builder()
                .id(client.getId())
                .name(client.getName())
                .projectId(client.getProjectId())
                .organizationId(client.getOrganizationId())
                .clientId(client.getClientId())
                .apiKeyHash(client.getApiKeyHash())
                .rateLimitRpm(client.getRateLimitRpm())
                .rateLimitBurst(client.getRateLimitBurst())
                .active(false)
                .createdAt(client.getCreatedAt())
                .updatedAt(Instant.now())
                .build();
        apiClientRepository.save(deactivated);
    }

}
