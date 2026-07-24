package com.nexus.nexusiam.presentation.controller;

import com.nexus.nexusiam.application.usecase.ApiClientTokenUseCaseImpl;
import com.nexus.nexusiam.application.usecase.BootstrapUseCaseImpl;
import com.nexus.nexusiam.application.usecase.RegisterUseCaseImpl;
import com.nexus.nexusiam.infrastructure.security.TokenBlacklistService;
import com.nexus.nexusiam.domain.model.ActorType;
import com.nexus.nexusiam.domain.model.AuditAction;
import com.nexus.nexusiam.domain.port.out.UserRepository;
import com.nexus.nexusiam.infrastructure.audit.AuditLogService;
import com.nexus.nexusiam.infrastructure.security.JwtProperties;
import com.nexus.nexusiam.infrastructure.security.JwtService;
import com.nexus.nexusiam.domain.model.ApiClient;
import com.nexus.nexusiam.domain.port.out.ApiClientRepository;
import com.nexus.nexusiam.presentation.dto.request.ApiClientRefreshRequest;
import com.nexus.nexusiam.presentation.dto.request.ApiClientTokenRequest;
import com.nexus.nexusiam.presentation.dto.request.BootstrapRequest;
import com.nexus.nexusiam.presentation.dto.request.LoginRequest;
import com.nexus.nexusiam.presentation.dto.request.RefreshTokenRequest;
import com.nexus.nexusiam.presentation.dto.request.RegisterRequest;
import com.nexus.nexusiam.presentation.dto.response.ApiClientTokenResponse;
import com.nexus.nexusiam.presentation.dto.response.AuthResponse;
import com.nexus.nexusiam.presentation.dto.response.BootstrapStatusResponse;
import com.nexus.nexusiam.presentation.dto.response.RegisterResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/iam/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User and API client authentication")
public class AuthController {

    private final BootstrapUseCaseImpl bootstrapUseCase;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;
    private final RegisterUseCaseImpl registerUseCaseImpl;
    private final ApiClientTokenUseCaseImpl apiClientTokenUseCaseImpl;
    private final ApiClientRepository apiClientRepository;
    private final AuditLogService auditLogService;
    private final TokenBlacklistService tokenBlacklistService;

    @Operation(summary = "Check if initial setup is required")
    @GetMapping("/setup/status")
    public ResponseEntity<BootstrapStatusResponse> setupStatus() {
        return ResponseEntity.ok(new BootstrapStatusResponse(bootstrapUseCase.isSetupRequired()));
    }

    @Operation(summary = "Create the first system admin — only works once, before any ADMIN exists")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Admin created, tokens returned"),
        @ApiResponse(responseCode = "403", description = "System already configured"),
        @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    @PostMapping("/setup")
    public ResponseEntity<AuthResponse> setup(@Valid @RequestBody BootstrapRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bootstrapUseCase.execute(request));
    }

    @Operation(summary = "Register organization + owner")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Organization and owner created"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "409", description = "Slug or email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        RegisterResponse response = registerUseCaseImpl.execute(request);
        auditLogService.log(AuditAction.USER_REGISTERED, response.ownerId().toString(), response.ownerEmail(), ActorType.SYSTEM,
                response.organizationId(), "ORGANIZATION", response.organizationId().toString(),
                java.util.Map.of("ownerEmail", response.ownerEmail(), "organizationSlug", response.organizationSlug()),
                httpRequest.getRemoteAddr());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "User login", description = "Returns access + refresh JWT tokens")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (BadCredentialsException e) {
            auditLogService.log(AuditAction.USER_LOGIN_FAILED, request.email(), request.email(), ActorType.USER,
                    null, "USER", null,
                    java.util.Map.of("email", request.email()),
                    httpRequest.getRemoteAddr());
            throw e;
        }

        var user = userRepository.findByEmail(request.email()).orElseThrow();

        String accessToken = jwtService.generateToken(
                user.getEmail().getValue(),
                user.getId(),
                user.getOrganizationId() != null ? user.getOrganizationId().toString() : "",
                user.getRole().name()
        );

        String refreshToken = jwtService.generateRefreshToken(user.getEmail().getValue());

        auditLogService.log(AuditAction.USER_LOGIN, user.getId().toString(), user.getName(), ActorType.USER,
                user.getOrganizationId(), "USER", user.getId().toString(),
                java.util.Map.of("email", user.getEmail().getValue()),
                httpRequest.getRemoteAddr());

        return ResponseEntity.ok(new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtProperties.getExpiration() / 1000,
                jwtProperties.getRefreshExpiration() / 1000
        ));
    }

    @Operation(summary = "Logout", description = "Revokes the current access token. Requires Bearer token.")
    @ApiResponse(responseCode = "204", description = "Token revoked")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            tokenBlacklistService.revoke(authHeader.substring(7));
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Issue API client token (M2M)", description = "Returns client_access + client_refresh tokens")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token issued"),
        @ApiResponse(responseCode = "401", description = "Invalid clientId or apiKey")
    })
    @PostMapping("/token")
    public ResponseEntity<ApiClientTokenResponse> token(@Valid @RequestBody ApiClientTokenRequest request, HttpServletRequest httpRequest) {
        ApiClientTokenResponse response = apiClientTokenUseCaseImpl.execute(request);
        auditLogService.log(AuditAction.API_CLIENT_TOKEN_ISSUED, request.clientId(), request.clientId(), ActorType.API_CLIENT,
                null, "API_CLIENT", request.clientId(),
                java.util.Map.of("clientId", request.clientId()),
                httpRequest.getRemoteAddr());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Refresh API client token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "New token issued"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    @PostMapping("/token/refresh")
    public ResponseEntity<ApiClientTokenResponse> tokenRefresh(@Valid @RequestBody ApiClientRefreshRequest request) {
        String refreshToken = request.refreshToken();

        if (!jwtService.isClientRefreshToken(refreshToken) || !jwtService.isTokenValid(refreshToken, jwtService.extractEmail(refreshToken))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String clientId = jwtService.extractEmail(refreshToken);
        ApiClient client = apiClientRepository.findByClientId(clientId)
                .filter(ApiClient::isActive)
                .orElse(null);

        if (client == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String newAccessToken = jwtService.generateClientToken(
                client.getClientId(),
                client.getId(),
                client.getOrganizationId().toString(),
                client.getProjectId().toString()
        );

        String newRefreshToken = jwtService.generateClientRefreshToken(client.getClientId());

        return ResponseEntity.ok(new ApiClientTokenResponse(
                newAccessToken,
                newRefreshToken,
                client.getClientId(),
                jwtProperties.getExpiration(),
                jwtProperties.getRefreshExpiration()
        ));
    }

    @Operation(summary = "Refresh user token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "New token issued"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        if (!jwtService.isRefreshToken(refreshToken) || !jwtService.isTokenValid(refreshToken, jwtService.extractEmail(refreshToken))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = jwtService.extractEmail(refreshToken);
        var user = userRepository.findByEmail(email).orElseThrow();

        String newAccessToken = jwtService.generateToken(
                user.getEmail().getValue(),
                user.getId(),
                user.getOrganizationId() != null ? user.getOrganizationId().toString() : "",
                user.getRole().name()
        );

        String newRefreshToken = jwtService.generateRefreshToken(user.getEmail().getValue());

        return ResponseEntity.ok(new AuthResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                jwtProperties.getExpiration() / 1000,
                jwtProperties.getRefreshExpiration() / 1000
        ));
    }
}
