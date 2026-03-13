package com.vikas.service;

import com.vikas.entity.Employee;
import com.vikas.ExceptionHandler.OnboardingException;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    /**
     * Simulates creating a work email account for the employee.
     * In production, replace this body with a real call to
     * Google Workspace Admin SDK, Microsoft Graph API, etc.
     *
     * @param employee the newly saved employee
     * @return the generated work email address
     * @throws OnboardingException if email creation fails
     */
    public String createWorkEmail(Employee employee) {
        try {
            // --- Replace with real API call ---
            String sanitizedName = employee.getName()
                    .toLowerCase()
                    .replaceAll("\\s+", ".");
            String email = sanitizedName + "@company.com";

            System.out.println("[EmailService] Work email created: " + email);
            return email;

        } catch (Exception ex) {
            throw new OnboardingException(
                    "EMAIL_CREATION",
                    "Failed to create work email for employee: " + employee.getName(),
                    ex
            );
        }
    }
}