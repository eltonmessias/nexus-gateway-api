package com.nexus.nexuscommons.exception;

import com.nexus.nexuscommons.NexusCommonsApplication;

public class NexusException extends RuntimeException{

    private final String code;

    public NexusException(String code, String message) {
        super(message);
        this.code = code;
    }

    public NexusException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
