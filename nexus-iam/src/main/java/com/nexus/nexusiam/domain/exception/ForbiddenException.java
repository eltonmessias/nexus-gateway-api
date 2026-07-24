package com.nexus.nexusiam.domain.exception;

/**
 * Raised when an authenticated caller lacks permission for an action,
 * or attempts to reach across organisation boundaries. Maps to HTTP 403.
 */
public class ForbiddenException extends IamException {
    public ForbiddenException(String message) {
        super("ACCESS_DENIED", message);
    }
}
