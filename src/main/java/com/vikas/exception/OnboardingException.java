package com.vikas.exception;

// CHANGED:
//  Package renamed from "com.vikas.ExceptionHandler" → "com.vikas.exception"
//  (Java convention: packages must be lowercase)
//
//  HOW TO MIGRATE:
//  1. Create a new folder: src/main/java/com/vikas/exception/
//  2. Create this file there: OnboardingException.java
//  3. Delete the old file from com/vikas/ExceptionHandler/OnboardingException.java
//  4. Update GlobalExceptionHandler.java import to: com.vikas.exception.OnboardingException

public class OnboardingException extends RuntimeException {

    private final String failedStep;

    public OnboardingException(String failedStep, String message) {
        super(message);
        this.failedStep = failedStep;
    }

    public OnboardingException(String failedStep, String message, Throwable cause) {
        super(message, cause);
        this.failedStep = failedStep;
    }

    public String getFailedStep() {
        return failedStep;
    }
}
