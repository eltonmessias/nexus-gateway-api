package com.nexus.nexusiam.domain.exception;

import java.util.UUID;

public class ProjectNotFoundException extends IamException  {
    public ProjectNotFoundException(UUID id) {
        super("PROJECT_NOT_FOUND", "Project not found with id: " + id);
    }

    public ProjectNotFoundException(String name) {
        super("PROJECT_NOT_FOUND", "Project not found with name: " + name);
    }
}
