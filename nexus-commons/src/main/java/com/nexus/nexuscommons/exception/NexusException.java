package com.nexus.nexuscommons.exception;

public class NexusException extends RuntimeException {
    private final String code;

    public NexusException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() { return code; }
}