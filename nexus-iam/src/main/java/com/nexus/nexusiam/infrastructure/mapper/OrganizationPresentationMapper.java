package com.nexus.nexusiam.infrastructure.mapper;

import com.nexus.nexusiam.domain.model.Organization;
import com.nexus.nexusiam.domain.model.valueobject.Slug;
import com.nexus.nexusiam.presentation.dto.request.OrganizationRequest;
import com.nexus.nexusiam.presentation.dto.response.OrganizationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface OrganizationPresentationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "slug", source = "slug", qualifiedByName = "stringToSlug")
    Organization toDomain(OrganizationRequest request);

    @Mapping(target = "slug", source = "slug", qualifiedByName = "slugToString")
    OrganizationResponse toResponse(Organization domain);

    @Named("stringToSlug")
    default Slug stringToSlug(String value){
        return value == null ? null : new Slug(value);
    }

    @Named("slugToString")
    default String slugToString(Slug slug){
        return slug == null ? null : slug.toString();
    }


}
