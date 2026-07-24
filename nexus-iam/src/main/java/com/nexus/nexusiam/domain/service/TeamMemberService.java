package com.nexus.nexusiam.domain.service;

import com.nexus.nexusiam.domain.exception.TeamNotFoundException;
import com.nexus.nexusiam.domain.model.TeamMember;
import com.nexus.nexusiam.domain.port.out.OrgMemberRepository;
import com.nexus.nexusiam.domain.port.out.TeamMemberRepository;
import com.nexus.nexusiam.domain.port.out.TeamRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final OrgMemberRepository orgMemberRepository;

    public TeamMember addMember(UUID teamId, UUID orgMemberId) {
        var team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException(teamId));
        var member = orgMemberRepository.findById(orgMemberId)
                .orElseThrow(() -> new IllegalArgumentException("Org member not found: " + orgMemberId));
        if (!member.getOrganizationId().equals(team.getOrganizationId())) {
            throw new IllegalArgumentException("Member does not belong to the team's organisation");
        }
        if (teamMemberRepository.existsByTeamIdAndOrgMemberId(teamId, orgMemberId)) {
            throw new IllegalStateException("Member is already in this team");
        }
        return teamMemberRepository.save(TeamMember.builder()
                .teamId(teamId)
                .orgMemberId(orgMemberId)
                .build());
    }

    public List<TeamMember> listMembers(UUID teamId) {
        teamRepository.findById(teamId).orElseThrow(() -> new TeamNotFoundException(teamId));
        return teamMemberRepository.findByTeamId(teamId);
    }

    public void removeMember(UUID teamId, UUID orgMemberId) {
        teamRepository.findById(teamId).orElseThrow(() -> new TeamNotFoundException(teamId));
        teamMemberRepository.deleteByTeamIdAndOrgMemberId(teamId, orgMemberId);
    }
}
