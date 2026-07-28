package com.nexus.nexusiam.application.usecase;

import com.nexus.nexusiam.domain.model.*;
import com.nexus.nexusiam.domain.model.valueobject.Email;
import com.nexus.nexusiam.domain.port.out.OrganizationRepository;
import com.nexus.nexusiam.domain.port.out.OrgMemberRepository;
import com.nexus.nexusiam.domain.port.out.UserRepository;
import com.nexus.nexusiam.infrastructure.security.JwtProperties;
import com.nexus.nexusiam.infrastructure.security.JwtService;
import com.nexus.nexusiam.presentation.dto.response.AuthResponse;
import com.nexus.nexusiam.presentation.dto.response.InvitationDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class InvitationUseCaseImpl {

    private final OrgMemberRepository orgMemberRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    /** Public: details shown on the accept-invite page. */
    public InvitationDetailsResponse getByToken(String token) {
        OrgMember member = validInvite(token);
        String orgName = organizationRepository.findById(member.getOrganizationId())
                .map(Organization::getName)
                .orElse("your organisation");
        return new InvitationDetailsResponse(orgName, member.getEmail(), member.getName(), member.getRole().name());
    }

    /** Public: the invitee sets a password; creates their account and logs them in. */
    @Transactional
    public AuthResponse accept(String token, String rawPassword) {
        OrgMember member = validInvite(token);
        if (userRepository.existsByEmail(member.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A user with this email already exists");
        }

        Instant now = Instant.now();
        User user = userRepository.save(User.builder()
                .name(member.getName())
                .email(new Email(member.getEmail()))
                .passwordHash(passwordEncoder.encode(rawPassword))
                .organizationId(member.getOrganizationId())
                .role(mapRole(member.getRole()))
                .createdAt(now)
                .updatedAt(now)
                .active(true)
                .build());

        // Activate the membership, link it to the new user, and consume the token.
        orgMemberRepository.save(OrgMember.builder()
                .id(member.getId())
                .organizationId(member.getOrganizationId())
                .userId(user.getId())
                .name(member.getName())
                .email(member.getEmail())
                .role(member.getRole())
                .status(OrgMemberStatus.ACTIVE)
                .joinedAt(member.getJoinedAt())
                .updatedAt(now)
                .inviteToken(null)
                .inviteExpiresAt(null)
                .build());

        String accessToken = jwtService.generateToken(
                user.getEmail().getValue(), user.getId(),
                user.getOrganizationId().toString(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail().getValue());

        return new AuthResponse(accessToken, refreshToken, "Bearer",
                jwtProperties.getExpiration() / 1000, jwtProperties.getRefreshExpiration() / 1000);
    }

    private OrgMember validInvite(String token) {
        OrgMember member = orgMemberRepository.findByInviteToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitation not found"));
        if (member.getStatus() != OrgMemberStatus.INVITED
                || member.getInviteExpiresAt() == null
                || member.getInviteExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.GONE, "This invitation is no longer valid");
        }
        return member;
    }

    private Role mapRole(OrgMemberRole role) {
        return switch (role) {
            case OWNER     -> Role.ORG_OWNER;
            case ADMIN     -> Role.TEAM_ADMIN;
            case DEVELOPER -> Role.TEAM_MEMBER;
            case VIEWER    -> Role.VIEWER;
        };
    }
}
