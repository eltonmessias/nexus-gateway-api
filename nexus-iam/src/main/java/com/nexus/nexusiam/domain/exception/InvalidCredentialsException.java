package com.nexus.nexusiam.domain.exception;

public class InvalidCredentialsException extends IamException {
    public InvalidCredentialsException() {
        super("INVALID_CREDENTIALS", "Invalid email or password");
    }
}