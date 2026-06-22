package com.nexus.nexusiam.application.usecase;

import com.nexus.nexusiam.domain.exception.ApiClientNotFoundException;
import com.nexus.nexusiam.domain.exception.InvalidCredentialsException;
import com.nexus.nexusiam.domain.model.ApiClient;
import com.nexus.nexusiam.domain.port.out.ApiClientRepository;
import com.nexus.nexusiam.infrastructure.security.JwtProperties;
import com.nexus.nexusiam.infrastructure.security.JwtService;
import com.nexus.nexusiam.presentation.dto.request.ApiClientTokenRequest;
import com.nexus.nexusiam.presentation.dto.response.ApiClientTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApiClientTokenUseCaseImpl {

    private final ApiClientRepository apiClientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public ApiClientTokenResponse execute(ApiClientTokenRequest request) {
        ApiClient client = apiClientRepository.findByClientId(request.clientId())
                .orElseThrow(() -> new ApiClientNotFoundException(request.clientId()));

        if (!client.isActive()) {
            throw new InvalidCredentialsException();
        }

        if (!passwordEncoder.matches(request.apiKey(), client.getApiKeyHash())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtService.generateClientToken(
                client.getClientId(),
                client.getId(),
                client.getOrganizationId().toString(),
                client.getProjectId().toString()
        );

        return new ApiClientTokenResponse(
                accessToken,
                client.getClientId(),
                jwtProperties.getExpiration()
        );
    }
}
