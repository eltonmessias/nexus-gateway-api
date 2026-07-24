package com.nexus.nexusiam.application.usecase;


import com.nexus.nexuscommons.dto.response.PagedResult;
import com.nexus.nexusiam.domain.model.ApiClient;
import com.nexus.nexusiam.domain.port.in.ApiClientUseCase;
import com.nexus.nexusiam.domain.service.ApiClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ApiClientUseCaseImpl implements ApiClientUseCase {

    private final ApiClientService apiClientService;
    private final PasswordEncoder passwordEncoder;

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
    public PagedResult<ApiClient> findAll(int page, int size) {
        long total = apiClientService.count();
        List<ApiClient> clients = apiClientService.findAll(page, size);
        return PagedResult.of(clients, page, size, total);
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

    @Override
    public String rotateApiKey(UUID id) {
        String newApiKey = generateApiKey();
        apiClientService.rotateApiKey(id, passwordEncoder.encode(newApiKey));
        return newApiKey;
    }

    private String generateApiKey() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return "nexus_live_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
