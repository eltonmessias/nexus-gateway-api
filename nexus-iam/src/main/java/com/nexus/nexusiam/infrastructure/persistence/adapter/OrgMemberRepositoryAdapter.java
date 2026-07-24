package com.nexus.nexusiam.infrastructure.persistence.adapter;

import com.nexus.nexusiam.domain.model.OrgMember;
import com.nexus.nexusiam.domain.port.out.OrgMemberRepository;
import com.nexus.nexusiam.infrastructure.mapper.OrgMemberMapper;
import com.nexus.nexusiam.infrastructure.persistence.repository.OrgMemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrgMemberRepositoryAdapter implements OrgMemberRepository {

    private final OrgMemberJpaRepository jpaRepository;
    private final OrgMemberMapper mapper;

    @Override
    public OrgMember save(OrgMember member) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(member)));
    }

    @Override
    public Optional<OrgMember> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<OrgMember> findAllByOrganizationId(UUID organizationId) {
        return jpaRepository.findAllByOrganizationId(organizationId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByOrganizationIdAndEmail(UUID organizationId, String email) {
        return jpaRepository.existsByOrganizationIdAndEmail(organizationId, email);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
