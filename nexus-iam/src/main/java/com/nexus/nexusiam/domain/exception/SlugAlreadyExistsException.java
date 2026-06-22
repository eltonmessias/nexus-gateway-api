package com.nexus.nexusiam.domain.exception;

public class SlugAlreadyExistsException extends IamException {
    public SlugAlreadyExistsException(String slug) {
        super("SLUG_ALREADY_EXISTS", "Slug already in use: " + slug);
    }
}