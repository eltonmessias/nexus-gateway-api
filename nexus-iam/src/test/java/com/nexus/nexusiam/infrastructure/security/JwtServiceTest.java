package com.nexus.nexusiam.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        properties.setExpiration(900000L);
        properties.setRefreshExpiration(604800000L);
        jwtService = new JwtService(properties);
    }

    @Test
    void generateToken_shouldContainCorrectClaims() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken("user@nexus.com", userId, "org-123", "ORG_OWNER");

        assertThat(jwtService.extractEmail(token)).isEqualTo("user@nexus.com");
        assertThat(jwtService.extractUserId(token)).isEqualTo(userId);
        assertThat(jwtService.extractRole(token)).isEqualTo("ORG_OWNER");
        assertThat(jwtService.extractTokenType(token)).isEqualTo("access");
    }

    @Test
    void generateRefreshToken_shouldHaveRefreshType() {
        String token = jwtService.generateRefreshToken("user@nexus.com");

        assertThat(jwtService.extractTokenType(token)).isEqualTo("refresh");
        assertThat(jwtService.isRefreshToken(token)).isTrue();
    }

    @Test
    void generateClientToken_shouldHaveClientAccessType() {
        UUID clientUUID = UUID.randomUUID();
        String token = jwtService.generateClientToken("nexus_abc123", clientUUID, "org-123", "proj-456");

        assertThat(jwtService.extractTokenType(token)).isEqualTo("client_access");
        assertThat(jwtService.extractEmail(token)).isEqualTo("nexus_abc123");
    }

    @Test
    void isTokenValid_shouldReturnTrue_whenTokenIsValid() {
        String token = jwtService.generateToken("user@nexus.com", UUID.randomUUID(), "org-123", "VIEWER");

        assertThat(jwtService.isTokenValid(token, "user@nexus.com")).isTrue();
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenEmailDoesNotMatch() {
        String token = jwtService.generateToken("user@nexus.com", UUID.randomUUID(), "org-123", "VIEWER");

        assertThat(jwtService.isTokenValid(token, "other@nexus.com")).isFalse();
    }

    @Test
    void isRefreshToken_shouldReturnFalse_forAccessToken() {
        String token = jwtService.generateToken("user@nexus.com", UUID.randomUUID(), "org-123", "VIEWER");

        assertThat(jwtService.isRefreshToken(token)).isFalse();
    }
}
