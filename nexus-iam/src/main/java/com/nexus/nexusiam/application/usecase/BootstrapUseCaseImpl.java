package com.nexus.nexusiam.application.usecase;

import com.nexus.nexusiam.domain.model.Role;
import com.nexus.nexusiam.domain.model.User;
import com.nexus.nexusiam.domain.model.valueobject.Email;
import com.nexus.nexusiam.domain.port.out.UserRepository;
import com.nexus.nexusiam.domain.service.UserService;
import com.nexus.nexusiam.infrastructure.security.JwtProperties;
import com.nexus.nexusiam.infrastructure.security.JwtService;
import com.nexus.nexusiam.presentation.dto.request.BootstrapRequest;
import com.nexus.nexusiam.presentation.dto.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class BootstrapUseCaseImpl {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public boolean isSetupRequired() {
        return !userRepository.existsByRole(Role.ADMIN);
    }

    public AuthResponse execute(BootstrapRequest request) {
        if (!isSetupRequired()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "System is already configured");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        Instant now = Instant.now();

        User admin = userService.create(User.builder()
                .name(request.name())
                .email(new Email(request.email()))
                .passwordHash(passwordEncoder.encode(request.password()))
                .organizationId(null)
                .role(Role.ADMIN)
                .createdAt(now)
                .updatedAt(now)
                .active(true)
                .build());

        String accessToken = jwtService.generateToken(
                admin.getEmail().getValue(),
                admin.getId(),
                "",
                admin.getRole().name()
        );

        String refreshToken = jwtService.generateRefreshToken(admin.getEmail().getValue());

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtProperties.getExpiration() / 1000,
                jwtProperties.getRefreshExpiration() / 1000
        );
    }
}
