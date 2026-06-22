package com.nexus.nexusiam.domain.exception;

import java.util.UUID;

public class OrganizationNotFoundException extends IamException {
    public OrganizationNotFoundException(UUID id) {
        super("USER_NOT_FOUND", "User not found with id: " + id);
    }
}
