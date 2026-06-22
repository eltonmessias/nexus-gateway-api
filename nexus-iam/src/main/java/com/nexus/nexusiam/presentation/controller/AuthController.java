package com.nexus.nexusiam.presentation.controller;

import com.nexus.nexusiam.application.usecase.ApiClientTokenUseCaseImpl;
import com.nexus.nexusiam.application.usecase.RegisterUseCaseImpl;
import com.nexus.nexusiam.domain.port.out.UserRepository;
import com.nexus.nexusiam.infrastructure.security.JwtProperties;
import com.nexus.nexusiam.infrastructure.security.JwtService;
import com.nexus.nexusiam.presentation.dto.request.ApiClientTokenRequest;
import com.nexus.nexusiam.presentation.dto.request.LoginRequest;
import com.nexus.nexusiam.presentation.dto.request.RefreshTokenRequest;
import com.nexus.nexusiam.presentation.dto.request.RegisterRequest;
import com.nexus.nexusiam.presentation.dto.response.ApiClientTokenResponse;
import com.nexus.nexusiam.presentation.dto.response.AuthResponse;
import com.nexus.nexusiam.presentation.dto.response.RegisterResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registerUseCaseImpl.execute(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        var user = userRepository.findByEmail(request.email()).orElseThrow();

        String accessToken = jwtService.generateToken(
                user.getEmail().getValue(),
                user.getId(),
                user.getOrganizationId() != null ? user.getOrganizationId().toString() : "",
                user.getRole().name()
        );

        String refreshToken = jwtService.generateRefreshToken(user.getEmail().getValue());

        return ResponseEntity.ok(new AuthResponse(
                accessToken,
                refreshToken,
                user.getEmail().getValue(),
                jwtProperties.getExpiration(),
                jwtProperties.getRefreshExpiration()
        ));
    }

    @PostMapping("/token")
    public ResponseEntity<ApiClientTokenResponse> token(@Valid @RequestBody ApiClientTokenRequest request) {
        return ResponseEntity.ok(apiClientTokenUseCaseImpl.execute(request));
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
