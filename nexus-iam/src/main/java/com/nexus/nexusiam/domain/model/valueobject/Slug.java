package com.nexus.nexusiam.domain.model.valueobject;

public class Slug {
    private final String value;
    private static final String SLUG_REGEX = "^[a-z0-9]+(?:-[a-z0-9]+)*$";

    public Slug(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value is null");
        }
        if (!value.matches(SLUG_REGEX)) {
            throw new IllegalArgumentException("Invalid Slug: " + value);
        }
        this.value = value;
    }

    public String getValue() { return value; }
}
