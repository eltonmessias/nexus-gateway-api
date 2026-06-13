package com.nexus.nexusiam.infrastructure.mapper;

import com.nexus.nexusiam.domain.model.Team;
import com.nexus.nexusiam.presentation.dto.request.TeamRequest;
import com.nexus.nexusiam.presentation.dto.response.TeamResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TeamPresentationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", constant = "true")
    Team toDomain(TeamRequest request);

    TeamResponse toResponse(Team domain);
}
