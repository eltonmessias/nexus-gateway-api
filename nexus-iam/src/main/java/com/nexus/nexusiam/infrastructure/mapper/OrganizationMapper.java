package com.nexus.nexusiam.infrastructure.mapper;

import com.nexus.nexusiam.domain.model.Organization;
import com.nexus.nexusiam.domain.model.valueobject.Slug;
import com.nexus.nexusiam.infrastructure.persistence.entity.OrganizationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {


    @Mapping(target = "slug", source = "slug", qualifiedByName = "slugToString")
    OrganizationEntity toEntity(Organization domain);

    @Mapping(target = "slug", source = "slug", qualifiedByName = "stringToString")
    Organization toDomain(OrganizationEntity entity);

    @Named("slugToString")
    default String slugToString(Slug slug) {
        return slug == null ? null : slug.getValue();
    }

    @Named("stringToSlug")
    default Slug stringToSlug(String value) {
        return value == null ? null : new Slug(value);
    }
}
