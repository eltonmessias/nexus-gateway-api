package com.nexus.nexusiam.domain.exception;

public class EmailAlreadyExistsException extends IamException {
    public EmailAlreadyExistsException(String email) {
        super("EMAIL_ALREADY_EXISTS", "Email already in use: " + email);
    }
}