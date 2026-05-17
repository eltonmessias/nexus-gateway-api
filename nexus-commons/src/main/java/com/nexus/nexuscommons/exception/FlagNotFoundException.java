package com.nexus.nexuscommons.exception;

public class FlagNotFoundException extends NexusException{

    public FlagNotFoundException(String flagKey) {
        super("FLAG_NOT_FOUND",
                String.format("Feature flag '%s' not found", flagKey));
    }
}
