package com.nexus.nexusiam.domain.port.in;

import com.nexus.nexusiam.domain.model.TeamMember;

import java.util.List;
import java.util.UUID;

public interface TeamMemberUseCase {
    TeamMember addMember(UUID teamId, UUID orgMemberId);
    List<TeamMember> listMembers(UUID teamId);
    void removeMember(UUID teamId, UUID orgMemberId);
}
