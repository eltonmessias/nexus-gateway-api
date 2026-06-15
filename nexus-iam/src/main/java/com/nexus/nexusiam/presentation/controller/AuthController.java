package com.nexus.nexusiam.presentation.controller;


import com.nexus.nexusiam.domain.port.out.UserRepository;
import com.nexus.nexusiam.infrastructure.security.JwtProperties;
import com.nexus.nexusiam.infrastructure.security.JwtService;
import com.nexus.nexusiam.presentation.dto.request.LoginRequest;
import com.nexus.nexusiam.presentation.dto.response.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/iam/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        var user = userRepository.findByEmail(request.email()).orElseThrow();

        String token = jwtService.generateToken(
                user.getEmail().getValue(),
                user.getId(),
                user.getOrganizationId() != null ? user.getOrganizationId().toString() : ""
        );

        return ResponseEntity.ok(new AuthResponse(token, user.getEmail().getValue(), jwtProperties.getExpiration()));
    }

}
