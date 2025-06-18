package com.nhnacademy.illuwa.common.exception;

public class AlreadyExistsEmailException extends RuntimeException {
    public AlreadyExistsEmailException(String message) {
        super(message);
    }
}
