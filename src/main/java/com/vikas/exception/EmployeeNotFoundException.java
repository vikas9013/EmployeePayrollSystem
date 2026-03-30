package com.vikas.exception;

// NEW CLASS
// Location: src/main/java/com/vikas/exception/EmployeeNotFoundException.java
// Purpose : Replaces the generic NoSuchElementException used in services.
//           Gives clearer intent and allows fine-grained handling in GlobalExceptionHandler.
//
// NOTE: Also rename the existing package from "ExceptionHandler" to "exception"
//       to follow Java naming conventions (packages must be lowercase).

public class EmployeeNotFoundException extends RuntimeException {

    private final Long employeeId;

    public EmployeeNotFoundException(Long id) {
        super("Employee with ID " + id + " not found.");
        this.employeeId = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }
}
