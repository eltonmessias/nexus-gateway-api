package com.nexus.nexusiam.domain.exception;

public abstract class IamException extends RuntimeException {
    private final String code;

    protected IamException(String code, String message) {
      super(message);
      this.code = code;
    }

    public String getCode() {
      return code;
    }
}
