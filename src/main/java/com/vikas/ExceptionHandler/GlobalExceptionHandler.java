package com.vikas.ExceptionHandler;

import com.vikas.exception.EmployeeNotFoundException;
import com.vikas.exception.OnboardingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// CHANGED:
//  1. Added @Slf4j — logs every exception with its cause for production debugging
//  2. Added handler for EmployeeNotFoundException (our new domain exception)
//  3. Added handler for AccessDeniedException — returns 403 instead of Spring's default
//  4. Added handler for generic RuntimeException — catches auth failures (wrong password etc.)
//  5. Updated OnboardingException import to com.vikas.exception (lowercase package)
//
// NOTE: Keep this class in "ExceptionHandler" package for now to avoid moving files.
//       In a future refactor, rename the package to "exception" (lowercase).

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ─── EMPLOYEE NOT FOUND (404) ────────────────────────────────────────────

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEmployeeNotFound(
            EmployeeNotFoundException ex) {
        log.warn("Employee not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // ─── GENERIC NOT FOUND — kept for other NoSuchElementException uses ──────

    @ExceptionHandler(java.util.NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            java.util.NoSuchElementException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // ─── BAD REQUEST (400) ───────────────────────────────────────────────────

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // ─── VALIDATION FAILED (400) ─────────────────────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed");
        log.warn("Validation failed: {}", message);
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    // ─── ONBOARDING FAILED (500) ─────────────────────────────────────────────

    @ExceptionHandler(OnboardingException.class)
    public ResponseEntity<Map<String, Object>> handleOnboardingFailure(OnboardingException ex) {
        log.error("Onboarding failed at step [{}]: {}", ex.getFailedStep(), ex.getMessage(), ex);
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp",  LocalDateTime.now().toString());
        body.put("status",     HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error",      "Onboarding Failed");
        body.put("failedStep", ex.getFailedStep());
        body.put("message",    ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // ─── ACCESS DENIED (403) ─────────────────────────────────────────────────

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN,
                "You do not have permission to perform this action.");
    }

    // ─── AUTH / RUNTIME ERRORS (401 / 500) ───────────────────────────────────

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        // Auth failures (wrong password, user not found) bubble up as RuntimeException
        if (ex.getMessage() != null && ex.getMessage().contains("Invalid username or password")) {
            log.warn("Authentication failure: {}", ex.getMessage());
            return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
        }
        log.error("Unexpected runtime error: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.");
    }

    // ─── PRIVATE HELPER ──────────────────────────────────────────────────────

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    status.value());
        body.put("error",     status.getReasonPhrase());
        body.put("message",   message);
        return ResponseEntity.status(status).body(body);
    }
}