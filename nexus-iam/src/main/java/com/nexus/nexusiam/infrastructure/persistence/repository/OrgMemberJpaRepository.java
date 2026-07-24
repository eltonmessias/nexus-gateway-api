package com.nexus.nexusiam.infrastructure.persistence.repository;

import com.nexus.nexusiam.infrastructure.persistence.entity.OrgMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrgMemberJpaRepository extends JpaRepository<OrgMemberEntity, UUID> {
    List<OrgMemberEntity> findAllByOrganizationId(UUID organizationId);
    boolean existsByOrganizationIdAndEmail(UUID organizationId, String email);
}
