package com.nexus.nexusiam.domain.port.out;

import com.nexus.nexusiam.domain.model.OrgMember;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrgMemberRepository {
    OrgMember save(OrgMember member);
    Optional<OrgMember> findById(UUID id);
    Optional<OrgMember> findByInviteToken(String inviteToken);
    List<OrgMember> findAllByOrganizationId(UUID organizationId);
    boolean existsByOrganizationIdAndEmail(UUID organizationId, String email);
    void deleteById(UUID id);
}
