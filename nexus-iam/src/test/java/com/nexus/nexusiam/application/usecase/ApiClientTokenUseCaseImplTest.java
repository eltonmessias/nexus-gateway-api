package com.nexus.nexusiam.application.usecase;

import com.nexus.nexusiam.domain.exception.ApiClientNotFoundException;
import com.nexus.nexusiam.domain.exception.InvalidCredentialsException;
import com.nexus.nexusiam.domain.model.ApiClient;
import com.nexus.nexusiam.domain.port.out.ApiClientRepository;
import com.nexus.nexusiam.infrastructure.security.JwtProperties;
import com.nexus.nexusiam.infrastructure.security.JwtService;
import com.nexus.nexusiam.presentation.dto.request.ApiClientTokenRequest;
import com.nexus.nexusiam.presentation.dto.response.ApiClientTokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiClientTokenUseCaseImplTest {

    @Mock private ApiClientRepository apiClientRepository;

    private ApiClientTokenUseCaseImpl useCase;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private JwtProperties jwtProperties;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        jwtProperties = new JwtProperties();
        jwtProperties.setSecret("404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        jwtProperties.setExpiration(900000L);
        jwtProperties.setRefreshExpiration(604800000L);
        jwtService = new JwtService(jwtProperties);

        useCase = new ApiClientTokenUseCaseImpl(apiClientRepository, passwordEncoder, jwtService, jwtProperties);
    }

    @Test
    void execute_shouldReturnToken_whenCredentialsAreValid() {
        String rawApiKey = "nexus_live_abc123";
        String clientId = "nexus_abc";

        ApiClient client = ApiClient.builder()
                .id(UUID.randomUUID())
                .clientId(clientId)
                .apiKeyHash(passwordEncoder.encode(rawApiKey))
                .organizationId(UUID.randomUUID())
                .projectId(UUID.randomUUID())
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(apiClientRepository.findByClientId(clientId)).thenReturn(Optional.of(client));

        ApiClientTokenResponse response = useCase.execute(new ApiClientTokenRequest(clientId, rawApiKey));

        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.clientId()).isEqualTo(clientId);
        assertThat(jwtService.extractTokenType(response.accessToken())).isEqualTo("client_access");
    }

    @Test
    void execute_shouldThrow_whenClientNotFound() {
        when(apiClientRepository.findByClientId("nexus_unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new ApiClientTokenRequest("nexus_unknown", "key")))
                .isInstanceOf(ApiClientNotFoundException.class);
    }

    @Test
    void execute_shouldThrow_whenApiKeyIsWrong() {
        String clientId = "nexus_abc";
        ApiClient client = ApiClient.builder()
                .id(UUID.randomUUID())
                .clientId(clientId)
                .apiKeyHash(passwordEncoder.encode("correct_key"))
                .organizationId(UUID.randomUUID())
                .projectId(UUID.randomUUID())
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(apiClientRepository.findByClientId(clientId)).thenReturn(Optional.of(client));

        assertThatThrownBy(() -> useCase.execute(new ApiClientTokenRequest(clientId, "wrong_key")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void execute_shouldThrow_whenClientIsInactive() {
        String clientId = "nexus_abc";
        String rawApiKey = "nexus_live_abc123";
        ApiClient client = ApiClient.builder()
                .id(UUID.randomUUID())
                .clientId(clientId)
                .apiKeyHash(passwordEncoder.encode(rawApiKey))
                .organizationId(UUID.randomUUID())
                .projectId(UUID.randomUUID())
                .active(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(apiClientRepository.findByClientId(clientId)).thenReturn(Optional.of(client));

        assertThatThrownBy(() -> useCase.execute(new ApiClientTokenRequest(clientId, rawApiKey)))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
