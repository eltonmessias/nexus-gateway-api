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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUseCaseImplTest {

    @Mock private OrganizationService organizationService;
    @Mock private UserService userService;
    @Mock private OrganizationRepository organizationRepository;
    @Mock private UserRepository userRepository;

    private RegisterUseCaseImpl useCase;
    private JwtService jwtService;
    private JwtProperties jwtProperties;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setSecret("404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        jwtProperties.setExpiration(900000L);
        jwtProperties.setRefreshExpiration(604800000L);
        jwtService = new JwtService(jwtProperties);

        useCase = new RegisterUseCaseImpl(
                organizationService, userService,
                organizationRepository, userRepository,
                new BCryptPasswordEncoder(), jwtService, jwtProperties);
    }

    @Test
    void execute_shouldCreateOrganizationAndOwner() {
        RegisterRequest request = new RegisterRequest(
                "Nexus Bank", "nexus-bank", "Core banking",
                "Admin", "admin@nexusbank.com", "Admin123!");

        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(organizationRepository.existsBySlug(any())).thenReturn(false);
        when(organizationService.create(any())).thenReturn(Organization.builder()
                .id(orgId).name("Nexus Bank").slug(new Slug("nexus-bank"))
                .createdAt(Instant.now()).updatedAt(Instant.now()).active(true).build());
        when(userService.create(any())).thenReturn(User.builder()
                .id(userId).name("Admin").email(new Email("admin@nexusbank.com"))
                .passwordHash("hashed").organizationId(orgId).role(Role.ORG_OWNER)
                .createdAt(Instant.now()).updatedAt(Instant.now()).active(true).build());

        RegisterResponse response = useCase.execute(request);

        assertThat(response.organizationId()).isEqualTo(orgId);
        assertThat(response.ownerId()).isEqualTo(userId);
        assertThat(response.ownerEmail()).isEqualTo("admin@nexusbank.com");
        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.refreshToken()).isNotBlank();
    }

    @Test
    void execute_shouldThrow_whenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest(
                "Nexus Bank", "nexus-bank", "Core banking",
                "Admin", "admin@nexusbank.com", "Admin123!");

        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    void execute_shouldThrow_whenSlugAlreadyExists() {
        RegisterRequest request = new RegisterRequest(
                "Nexus Bank", "nexus-bank", "Core banking",
                "Admin", "admin@nexusbank.com", "Admin123!");

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(organizationRepository.existsBySlug(any())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(SlugAlreadyExistsException.class);
    }
}
