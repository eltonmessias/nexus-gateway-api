package com.nexus.nexusiam.infrastructure.mapper;

import com.nexus.nexusiam.domain.model.Team;
import com.nexus.nexusiam.infrastructure.persistence.entity.TeamEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    Team toDomain(TeamEntity entity);
    TeamEntity toEntity(Team domain);
}
