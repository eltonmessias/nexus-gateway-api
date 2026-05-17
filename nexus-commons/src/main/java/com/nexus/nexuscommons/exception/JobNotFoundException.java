package com.nexus.nexuscommons.exception;

import java.util.UUID;

public class JobNotFoundException extends NexusException{


    public JobNotFoundException(UUID jobId) {
        super("JOB_NOT_FOUND",
                String.format("Job '%s' not found", jobId));
    }
}
