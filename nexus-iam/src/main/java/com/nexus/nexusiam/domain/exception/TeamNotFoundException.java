package com.nexus.nexusiam.domain.exception;

import java.util.UUID;

public class TeamNotFoundException extends IamException {
    public TeamNotFoundException(UUID id) {
        super("TEAM_NOT_FOUND", "Team not found with id: " + id);
    }
}
