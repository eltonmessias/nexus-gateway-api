package com.nexus.nexusiam.infrastructure.mapper;

import com.nexus.nexusiam.domain.model.TeamMember;
import com.nexus.nexusiam.presentation.dto.response.TeamMemberResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TeamMemberPresentationMapper {

    TeamMemberResponse toResponse(TeamMember domain);
}
