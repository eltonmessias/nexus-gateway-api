package com.nexus.nexusiam.infrastructure.mapper;


import com.nexus.nexusiam.domain.model.ApiClient;
import com.nexus.nexusiam.presentation.dto.request.ApiClientRequest;
import com.nexus.nexusiam.presentation.dto.response.ApiClientResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;


@Mapper(componentModel = "spring")
public interface ApiClientPresentationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clientId", ignore = true)
    @Mapping(target = "apiKeyHash", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "rateLimitRpm", source = "rateLimitRpm", defaultValue = "100")
    @Mapping(target = "rateLimitBurst", source = "rateLimitBurst", defaultValue = "20")
    ApiClient toDomain(ApiClientRequest request);

    ApiClientResponse toResponse(ApiClient domain);

}
