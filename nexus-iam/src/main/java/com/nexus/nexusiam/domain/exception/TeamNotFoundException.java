package com.nexus.nexusiam.domain.exception;

import java.util.UUID;

public class TeamNotFoundException extends RuntimeException {
    public TeamNotFoundException(UUID id) {

        super("Team not found with id: " + id);
    }
}
