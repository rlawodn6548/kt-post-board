package com.kt.login.exception;

import lombok.Getter;

@Getter
public class LoginFailedException extends RuntimeException {
    private final int failureCount;

    public LoginFailedException(String message, int failureCount) {
        super(message);
        this.failureCount = failureCount;
    }
}
