package com.nexus.nexusiam.infrastructure.persistence.repository;

import com.nexus.nexusiam.infrastructure.persistence.entity.TeamMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamMemberJpaRepository extends JpaRepository<TeamMemberEntity, UUID> {
    List<TeamMemberEntity> findAllByTeamId(UUID teamId);
    boolean existsByTeamIdAndOrgMemberId(UUID teamId, UUID orgMemberId);
    Optional<TeamMemberEntity> findByTeamIdAndOrgMemberId(UUID teamId, UUID orgMemberId);
    void deleteByTeamIdAndOrgMemberId(UUID teamId, UUID orgMemberId);
}
