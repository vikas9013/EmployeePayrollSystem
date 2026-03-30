package com.vikas.service;

import com.vikas.entity.Employee;
import com.vikas.exception.OnboardingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// CHANGED:
//  1. Added @Slf4j — replaces System.out.println with structured logging
//  2. Updated OnboardingException import path to com.vikas.exception (lowercase package)

@Slf4j
@Service
public class EmailService {

    /**
     * Simulates creating a work email account for the employee.
     * In production, replace the body with a real call to
     * Google Workspace Admin SDK or Microsoft Graph API.
     */
    public String createWorkEmail(Employee employee) {
        try {
            String sanitizedName = employee.getName()
                    .toLowerCase()
                    .replaceAll("\\s+", ".");
            String email = sanitizedName + "@company.com";

            log.info("[EmailService] Work email created: {}", email);
            return email;

        } catch (Exception ex) {
            log.error("[EmailService] Failed to create work email for: {}",
                    employee.getName(), ex);
            throw new OnboardingException(
                    "EMAIL_CREATION",
                    "Failed to create work email for employee: " + employee.getName(),
                    ex
            );
        }
    }
}