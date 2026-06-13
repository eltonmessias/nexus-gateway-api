package com.nexus.nexusiam.domain.exception;

import java.util.UUID;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(UUID id) {
      super("Projeect with id " + id + " not found");
    }

    public ProjectNotFoundException(String name) {
      super("Project with name " + name + " not found");
    }
}
