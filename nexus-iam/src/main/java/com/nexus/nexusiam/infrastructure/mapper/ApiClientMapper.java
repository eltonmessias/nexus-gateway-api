package com.nexus.nexusiam.infrastructure.mapper;

import com.nexus.nexusiam.domain.model.ApiClient;
import com.nexus.nexusiam.infrastructure.persistence.entity.ApiClientEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ApiClientMapper {
    ApiClient toDomain(ApiClientEntity entity);
    ApiClientEntity toEntity(ApiClient domain);
}
