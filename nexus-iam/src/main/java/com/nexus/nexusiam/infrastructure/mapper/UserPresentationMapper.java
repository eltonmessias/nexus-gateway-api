package com.nexus.nexusiam.infrastructure.mapper;

import com.nexus.nexusiam.domain.model.User;
import com.nexus.nexusiam.domain.model.valueobject.Email;
import com.nexus.nexusiam.presentation.dto.request.UserRequest;
import com.nexus.nexusiam.presentation.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserPresentationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "email", source = "email", qualifiedByName = "stringToEmail")
    @Mapping(target = "passwordHash", source = "password")
    User  toDomain(UserRequest request);

    @Mapping(target = "email", source = "email", qualifiedByName = "emailToString")
    UserResponse toResponse(User domain);

    @Named("stringToEmail")
    default Email stringToEmail(String value) {
        return value == null ? null : new Email(value);
    }

    @Named("emailToString")
    default String emailToString(Email email) {
        return email == null ? null : email.getValue();
    }
}
