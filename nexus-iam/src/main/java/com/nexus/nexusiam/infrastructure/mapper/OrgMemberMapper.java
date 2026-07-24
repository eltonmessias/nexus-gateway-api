package com.nexus.nexusiam.infrastructure.mapper;

import com.nexus.nexusiam.domain.model.OrgMember;
import com.nexus.nexusiam.infrastructure.persistence.entity.OrgMemberEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrgMemberMapper {
    OrgMember toDomain(OrgMemberEntity entity);
    OrgMemberEntity toEntity(OrgMember domain);
}
