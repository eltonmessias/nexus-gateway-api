package com.nexus.nexusiam.presentation.controller;

import com.nexus.nexusiam.application.usecase.ApiClientTokenUseCaseImpl;
import com.nexus.nexusiam.application.usecase.RegisterUseCaseImpl;
import com.nexus.nexusiam.domain.model.ActorType;
import com.nexus.nexusiam.domain.model.AuditAction;
import com.nexus.nexusiam.domain.port.out.UserRepository;
import com.nexus.nexusiam.infrastructure.audit.AuditLogService;
import com.nexus.nexusiam.infrastructure.security.JwtProperties;
import com.nexus.nexusiam.infrastructure.security.JwtService;
import com.nexus.nexusiam.presentation.dto.request.ApiClientTokenRequest;
import com.nexus.nexusiam.presentation.dto.request.LoginRequest;
import com.nexus.nexusiam.presentation.dto.request.RefreshTokenRequest;
import com.nexus.nexusiam.presentation.dto.request.RegisterRequest;
import com.nexus.nexusiam.presentation.dto.response.ApiClientTokenResponse;
import com.nexus.nexusiam.presentation.dto.response.AuthResponse;
import com.nexus.nexusiam.presentation.dto.response.RegisterResponse;
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
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;
    private final RegisterUseCaseImpl registerUseCaseImpl;
    private final ApiClientTokenUseCaseImpl apiClientTokenUseCaseImpl;
    private final AuditLogService auditLogService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        RegisterResponse response = registerUseCaseImpl.execute(request);
        auditLogService.log(AuditAction.USER_REGISTERED, response.ownerId().toString(), ActorType.SYSTEM,
                response.organizationId(), "ORGANIZATION", response.organizationId().toString(),
                java.util.Map.of("ownerEmail", response.ownerEmail(), "organizationSlug", response.organizationSlug()),
                httpRequest.getRemoteAddr());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (BadCredentialsException e) {
            auditLogService.log(AuditAction.USER_LOGIN_FAILED, request.email(), ActorType.USER,
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

        auditLogService.log(AuditAction.USER_LOGIN, user.getId().toString(), ActorType.USER,
                user.getOrganizationId(), "USER", user.getId().toString(),
                java.util.Map.of("email", user.getEmail().getValue()),
                httpRequest.getRemoteAddr());

        return ResponseEntity.ok(new AuthResponse(
                accessToken,
                refreshToken,
                user.getEmail().getValue(),
                jwtProperties.getExpiration(),
                jwtProperties.getRefreshExpiration()
        ));
    }

    @PostMapping("/token")
    public ResponseEntity<ApiClientTokenResponse> token(@Valid @RequestBody ApiClientTokenRequest request, HttpServletRequest httpRequest) {
        ApiClientTokenResponse response = apiClientTokenUseCaseImpl.execute(request);
        auditLogService.log(AuditAction.API_CLIENT_TOKEN_ISSUED, request.clientId(), ActorType.API_CLIENT,
                null, "API_CLIENT", request.clientId(),
                java.util.Map.of("clientId", request.clientId()),
                httpRequest.getRemoteAddr());
        return ResponseEntity.ok(response);
    }

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
                user.getEmail().getValue(),
                jwtProperties.getExpiration(),
                jwtProperties.getRefreshExpiration()
        ));
    }
}
