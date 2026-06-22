package com.nexus.nexusiam.domain.exception;

import java.util.UUID;

public class ApiClientNotFoundException extends IamException {
    public ApiClientNotFoundException(UUID id) {
        super("API_CLIENT_NOT_FOUND", "API Client not found with id: " + id);
    }

    public ApiClientNotFoundException(String clientId) {
        super("API_CLIENT_NOT_FOUND", "API Client not found with clientId: " + clientId);
    }
}
