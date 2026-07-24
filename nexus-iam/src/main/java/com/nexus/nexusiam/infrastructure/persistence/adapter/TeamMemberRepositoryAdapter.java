package com.nexus.nexusiam.infrastructure.persistence.adapter;

import com.nexus.nexusiam.domain.model.TeamMember;
import com.nexus.nexusiam.domain.port.out.TeamMemberRepository;
import com.nexus.nexusiam.infrastructure.mapper.TeamMemberMapper;
import com.nexus.nexusiam.infrastructure.persistence.entity.OrgMemberEntity;
import com.nexus.nexusiam.infrastructure.persistence.entity.TeamMemberEntity;
import com.nexus.nexusiam.infrastructure.persistence.repository.OrgMemberJpaRepository;
import com.nexus.nexusiam.infrastructure.persistence.repository.TeamMemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TeamMemberRepositoryAdapter implements TeamMemberRepository {

    private final TeamMemberJpaRepository jpaRepository;
    private final OrgMemberJpaRepository orgMemberJpaRepository;
    private final TeamMemberMapper mapper;

    @Override
    public TeamMember save(TeamMember member) {
        OrgMemberEntity orgMember = orgMemberJpaRepository.findById(member.getOrgMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Org member not found: " + member.getOrgMemberId()));

        TeamMemberEntity entity = TeamMemberEntity.builder()
                .teamId(member.getTeamId())
                .orgMember(orgMember)
                .joinedAt(Instant.now())
                .build();

        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<TeamMember> findByTeamId(UUID teamId) {
        return jpaRepository.findAllByTeamId(teamId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByTeamIdAndOrgMemberId(UUID teamId, UUID orgMemberId) {
        return jpaRepository.existsByTeamIdAndOrgMemberId(teamId, orgMemberId);
    }

    @Override
    @Transactional
    public void deleteByTeamIdAndOrgMemberId(UUID teamId, UUID orgMemberId) {
        jpaRepository.deleteByTeamIdAndOrgMemberId(teamId, orgMemberId);
    }
}
