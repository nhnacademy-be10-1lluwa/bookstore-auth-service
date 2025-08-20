package com.nhnacademy.illuwa.exception;

public class InvalidTokenException extends RuntimeException {
    private final String code;

    public InvalidTokenException(String code, String message) {
        super(message);
        this.code = code;
    }
    public InvalidTokenException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
