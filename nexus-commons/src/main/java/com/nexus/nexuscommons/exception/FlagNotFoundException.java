package com.nexus.nexuscommons.exception;

public class FlagNotFoundException extends NexusException {
    public FlagNotFoundException(String message) {
        super("FLAG_NOT_FOUND", message);
    }
}