package com.nexus.nexusiam.infrastructure.mapper;


import com.nexus.nexusiam.domain.model.User;
import com.nexus.nexusiam.domain.model.valueobject.Email;
import com.nexus.nexusiam.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "email", source = "email", qualifiedByName = "stringToEmail")
    User toDomain(UserEntity entity);

    @Mapping(target = "email", source = "email", qualifiedByName = "emailToString")
    UserEntity toEntity(User domain);

    @Named("emailToString")
    default String emailToString(Email email) {
        return email == null ? null : email.getValue();
    }

    @Named("stringToEmail")
    default Email stringToEmail(String value) {
        return value == null ? null : new Email(value);
    }
}
