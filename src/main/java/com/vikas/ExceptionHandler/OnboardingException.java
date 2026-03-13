package com.vikas.ExceptionHandler;

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
