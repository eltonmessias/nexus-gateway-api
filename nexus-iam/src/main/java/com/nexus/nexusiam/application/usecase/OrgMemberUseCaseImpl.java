package com.nexus.nexusiam.application.usecase;

import com.nexus.nexusiam.domain.exception.ForbiddenException;
import com.nexus.nexusiam.domain.model.OrgMember;
import com.nexus.nexusiam.domain.model.OrgMemberRole;
import com.nexus.nexusiam.domain.model.OrgMemberStatus;
import com.nexus.nexusiam.domain.port.in.OrgMemberUseCase;
import com.nexus.nexusiam.domain.port.out.AuthorizationPort;
import com.nexus.nexusiam.domain.port.out.OrgMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrgMemberUseCaseImpl implements OrgMemberUseCase {

    private final OrgMemberRepository orgMemberRepository;
    private final AuthorizationPort authorization;

    @Override
    public List<OrgMember> findByOrganization(UUID organizationId) {
        authorization.requireSameOrg(organizationId);
        return orgMemberRepository.findAllByOrganizationId(organizationId);
    }

    @Override
    public OrgMember invite(UUID organizationId, String name, String email, OrgMemberRole role) {
        authorization.requireOrgManager(organizationId);
        if (role == OrgMemberRole.OWNER) {
            throw new ForbiddenException("A member cannot be invited as OWNER");
        }
        if (orgMemberRepository.existsByOrganizationIdAndEmail(organizationId, email)) {
            throw new IllegalStateException("Member with email " + email + " already exists in this organisation");
        }
        Instant now = Instant.now();
        String inviteToken = UUID.randomUUID().toString().replace("-", "")
                + UUID.randomUUID().toString().replace("-", "");
        OrgMember member = OrgMember.builder()
                .organizationId(organizationId)
                .name(name != null ? name : email.split("@")[0])
                .email(email)
                .role(role)
                .status(OrgMemberStatus.INVITED)
                .joinedAt(now)
                .updatedAt(now)
                .inviteToken(inviteToken)
                .inviteExpiresAt(now.plus(7, ChronoUnit.DAYS))
                .build();
        return orgMemberRepository.save(member);
    }

    @Override
    public OrgMember changeRole(UUID memberId, OrgMemberRole role) {
        OrgMember existing = orgMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + memberId));
        authorization.requireOrgManager(existing.getOrganizationId());
        if (existing.getRole() == OrgMemberRole.OWNER) {
            throw new ForbiddenException("The organisation owner's role cannot be changed");
        }
        if (role == OrgMemberRole.OWNER) {
            throw new ForbiddenException("A member cannot be promoted to OWNER");
        }
        OrgMember updated = OrgMember.builder()
                .id(existing.getId())
                .organizationId(existing.getOrganizationId())
                .userId(existing.getUserId())
                .name(existing.getName())
                .email(existing.getEmail())
                .role(role)
                .status(existing.getStatus())
                .joinedAt(existing.getJoinedAt())
                .updatedAt(Instant.now())
                .inviteToken(existing.getInviteToken())
                .inviteExpiresAt(existing.getInviteExpiresAt())
                .build();
        return orgMemberRepository.save(updated);
    }

    @Override
    public void remove(UUID memberId) {
        OrgMember existing = orgMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + memberId));
        authorization.requireOrgManager(existing.getOrganizationId());
        if (existing.getRole() == OrgMemberRole.OWNER) {
            throw new ForbiddenException("The organisation owner cannot be removed");
        }
        orgMemberRepository.deleteById(memberId);
    }
}
