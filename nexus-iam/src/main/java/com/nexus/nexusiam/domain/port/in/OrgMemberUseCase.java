package com.nexus.nexusiam.domain.port.in;

import com.nexus.nexusiam.domain.model.OrgMember;
import com.nexus.nexusiam.domain.model.OrgMemberRole;

import java.util.List;
import java.util.UUID;

public interface OrgMemberUseCase {
    List<OrgMember> findByOrganization(UUID organizationId);
    OrgMember invite(UUID organizationId, String name, String email, OrgMemberRole role);
    OrgMember changeRole(UUID memberId, OrgMemberRole role);
    void remove(UUID memberId);
}
