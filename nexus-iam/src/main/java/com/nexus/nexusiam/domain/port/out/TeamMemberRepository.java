package com.nexus.nexusiam.domain.port.out;

import com.nexus.nexusiam.domain.model.TeamMember;

import java.util.List;
import java.util.UUID;

public interface TeamMemberRepository {
    TeamMember save(TeamMember member);
    List<TeamMember> findByTeamId(UUID teamId);
    boolean existsByTeamIdAndOrgMemberId(UUID teamId, UUID orgMemberId);
    void deleteByTeamIdAndOrgMemberId(UUID teamId, UUID orgMemberId);
}
