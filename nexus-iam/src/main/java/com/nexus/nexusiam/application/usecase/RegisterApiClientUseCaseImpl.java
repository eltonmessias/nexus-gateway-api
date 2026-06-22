package com.nexus.nexusiam.application.usecase;


import com.nexus.nexusiam.domain.model.ApiClient;
import com.nexus.nexusiam.domain.port.out.ApiClientRepository;
import com.nexus.nexusiam.domain.service.ApiClientService;
import com.nexus.nexusiam.presentation.dto.request.ApiClientRequest;
import com.nexus.nexusiam.presentation.dto.response.ApiClientCreatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RegisterApiClientUseCaseImpl {

    private final ApiClientService apiClientService;
    private final ApiClientRepository apiClientRepository;
    private final PasswordEncoder passwordEncoder;

    public ApiClientCreatedResponse execute(ApiClientRequest request) {
        String clientId = "nexus_" + UUID.randomUUID().toString().replace("-", "");
        String apiKey = generateApiKey();

        ApiClient client = ApiClient.builder()
                .name(request.name())
                .projectId(request.projectId())
                .organizationId(request.organizationId())
                .clientId(clientId)
                .apiKeyHash(passwordEncoder.encode(apiKey))
                .rateLimitRpm(request.rateLimitRpm() != null ? request.rateLimitRpm() : 100)
                .rateLimitBurst(request.rateLimitBurst() != null ? request.rateLimitBurst() : 20)
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        ApiClient saved = apiClientService.create(client);

        return new ApiClientCreatedResponse(
                saved.getId(),
                saved.getName(),
                saved.getProjectId(),
                saved.getOrganizationId(),
                saved.getClientId(),
                apiKey,
                saved.getRateLimitRpm(),
                saved.getRateLimitBurst()
        );
    }

    private String generateApiKey() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return "nexus_live_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
