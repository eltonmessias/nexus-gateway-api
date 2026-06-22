package com.nexus.nexusiam.domain.exception;

public class TokenExpiredException extends IamException {
    public TokenExpiredException() {
        super("TOKEN_EXPIRED", "JWT token has expired");
    }
}