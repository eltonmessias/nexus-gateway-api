package com.nexus.nexusiam.infrastructure.mapper;

import com.nexus.nexusiam.domain.model.TeamMember;
import com.nexus.nexusiam.infrastructure.persistence.entity.TeamMemberEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TeamMemberMapper {

    @Mapping(target = "orgMemberId",  source = "orgMember.id")
    @Mapping(target = "memberName",   source = "orgMember.name")
    @Mapping(target = "memberEmail",  source = "orgMember.email")
    @Mapping(target = "memberRole",   source = "orgMember.role")
    TeamMember toDomain(TeamMemberEntity entity);
}
