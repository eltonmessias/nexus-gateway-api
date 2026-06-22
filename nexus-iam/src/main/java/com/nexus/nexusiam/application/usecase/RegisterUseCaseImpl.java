package com.nexus.nexusiam.application.usecase;


import com.nexus.nexusiam.domain.exception.EmailAlreadyExistsException;
import com.nexus.nexusiam.domain.exception.SlugAlreadyExistsException;
import com.nexus.nexusiam.domain.model.Organization;
import com.nexus.nexusiam.domain.model.Role;
import com.nexus.nexusiam.domain.model.User;
import com.nexus.nexusiam.domain.model.valueobject.Email;
import com.nexus.nexusiam.domain.model.valueobject.Slug;
import com.nexus.nexusiam.domain.port.out.OrganizationRepository;
import com.nexus.nexusiam.domain.port.out.UserRepository;
import com.nexus.nexusiam.domain.service.OrganizationService;
import com.nexus.nexusiam.domain.service.UserService;
import com.nexus.nexusiam.infrastructure.security.JwtProperties;
import com.nexus.nexusiam.infrastructure.security.JwtService;
import com.nexus.nexusiam.presentation.dto.request.RegisterRequest;
import com.nexus.nexusiam.presentation.dto.response.RegisterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class RegisterUseCaseImpl {

    private final OrganizationService organizationService;
    private final UserService userService;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public RegisterResponse execute(RegisterRequest request) {
        if (userRepository.existsByEmail(request.ownerEmail())) {
            throw new EmailAlreadyExistsException(request.ownerEmail());
        }

        if (organizationRepository.existsBySlug(request.organizationSlug())) {
            throw new SlugAlreadyExistsException(request.organizationSlug());
        }

        Instant now = Instant.now();

        Organization organization = organizationService.create(Organization.builder()
                .name(request.organizationName())
                .slug(new Slug(request.organizationSlug()))
                .description(request.organizationDescription())
                .createdAt(now)
                .updatedAt(now)
                .active(true)
                .build());

        User owner = userService.create(User.builder()
                .name(request.ownerName())
                .email(new Email(request.ownerEmail()))
                .passwordHash(passwordEncoder.encode(request.ownerPassword()))
                .organizationId(organization.getId())
                .role(Role.ORG_OWNER)
                .createdAt(now)
                .updatedAt(now)
                .active(true)
                .build());

        String accessToken = jwtService.generateToken(
                owner.getEmail().getValue(),
                owner.getId(),
                organization.getId().toString(),
                owner.getRole().name()
        );

        String refreshToken = jwtService.generateRefreshToken(owner.getEmail().getValue());

        return new RegisterResponse(
                organization.getId(),
                organization.getName(),
                organization.getSlug().getValue(),
                owner.getId(),
                owner.getEmail().getValue(),
                accessToken,
                refreshToken,
                jwtProperties.getExpiration(),
                jwtProperties.getRefreshExpiration()
        );
    }
}
