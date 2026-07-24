package com.nexus.nexusiam.application.usecase;

import com.nexus.nexusiam.domain.exception.TeamNotFoundException;
import com.nexus.nexusiam.domain.model.Team;
import com.nexus.nexusiam.domain.model.TeamMember;
import com.nexus.nexusiam.domain.port.in.TeamMemberUseCase;
import com.nexus.nexusiam.domain.port.out.AuthorizationPort;
import com.nexus.nexusiam.domain.port.out.TeamRepository;
import com.nexus.nexusiam.domain.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamMemberUseCaseImpl implements TeamMemberUseCase {

    private final TeamMemberService teamMemberService;
    private final TeamRepository teamRepository;
    private final AuthorizationPort authorization;

    @Override
    public TeamMember addMember(UUID teamId, UUID orgMemberId) {
        authorization.requireOrgManager(orgIdOf(teamId));
        return teamMemberService.addMember(teamId, orgMemberId);
    }

    @Override
    public List<TeamMember> listMembers(UUID teamId) {
        authorization.requireSameOrg(orgIdOf(teamId));
        return teamMemberService.listMembers(teamId);
    }

    @Override
    public void removeMember(UUID teamId, UUID orgMemberId) {
        authorization.requireOrgManager(orgIdOf(teamId));
        teamMemberService.removeMember(teamId, orgMemberId);
    }

    private UUID orgIdOf(UUID teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException(teamId));
        return team.getOrganizationId();
    }
}
