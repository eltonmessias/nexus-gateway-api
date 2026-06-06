package com.nexus.nexuscommons.exception;

public class JobNotFoundException extends NexusException {
    public JobNotFoundException(String message) {
        super("JOB_NOT_FOUND",message);
    }
}