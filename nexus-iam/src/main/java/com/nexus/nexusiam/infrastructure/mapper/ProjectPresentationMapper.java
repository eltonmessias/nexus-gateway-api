package com.nexus.nexusiam.infrastructure.mapper;

import com.nexus.nexusiam.domain.model.Project;
import com.nexus.nexusiam.presentation.dto.request.ProjectRequest;
import com.nexus.nexusiam.presentation.dto.response.ProjectResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectPresentationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", constant = "true")
    Project toDomain(ProjectRequest request);

    ProjectResponse toResponse(Project domain);
}
