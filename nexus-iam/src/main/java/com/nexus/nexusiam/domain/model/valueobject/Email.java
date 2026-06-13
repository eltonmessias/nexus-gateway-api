package com.nexus.nexusiam.domain.model.valueobject;

public class Email {
    private final String value;
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    public Email(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Email value cannot be null");
        }
        if (!value.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("Email value is not a valid email address");
        }
        this.value = value;
    }

    public String getValue() { return value; }
}
