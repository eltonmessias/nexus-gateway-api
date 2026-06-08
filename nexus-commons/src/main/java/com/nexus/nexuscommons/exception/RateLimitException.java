package com.nexus.nexuscommons.exception;

public class RateLimitException extends NexusException {
    public RateLimitException(String message) {
        super("RATE_LIMIT_ERROR", message);
    }
}