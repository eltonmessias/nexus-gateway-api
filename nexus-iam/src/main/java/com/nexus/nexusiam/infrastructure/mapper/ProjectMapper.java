package com.nexus.nexusiam.infrastructure.mapper;

import com.nexus.nexusiam.domain.model.Project;
import com.nexus.nexusiam.infrastructure.persistence.entity.ProjectEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    Project toDomain(ProjectEntity entity);
    ProjectEntity toEntity(Project domain);
}
