package com.vikas.service;

import com.vikas.entity.Employee;
import com.vikas.exception.OnboardingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

// CHANGED:
//  1. Added @Slf4j — replaces System.out.println with structured logging
//  2. Updated OnboardingException import path to com.vikas.exception (lowercase package)

@Slf4j
@Service
public class TrainingService {

    /**
     * Simulates assigning training modules to the new hire based on their designation.
     * In production, replace the body with a real call to your LMS
     * (e.g. Cornerstone, Docebo, TalentLMS API).
     */
    public void assignTrainingModules(Employee employee) {
        try {
            List<String> modules = getLearningResources(employee.getDesignation());
            log.info("[TrainingService] Assigned modules to {}: {}", employee.getName(), modules);

        } catch (OnboardingException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("[TrainingService] Failed to assign training for: {}",
                    employee.getName(), ex);
            throw new OnboardingException(
                    "TRAINING_ASSIGNMENT",
                    "Failed to assign training modules for: " + employee.getName(),
                    ex
            );
        }
    }

    private List<String> getLearningResources(String designation) {
        if (designation == null) {
            return List.of("Company Orientation", "Code of Conduct");
        }

        return switch (designation.toLowerCase()) {
            case "engineer", "developer", "sde" ->
                    List.of("Company Orientation", "Secure Coding Practices", "Git Workflow");
            case "manager", "team lead" ->
                    List.of("Company Orientation", "Leadership Fundamentals", "HR Policies");
            case "hr", "human resources" ->
                    List.of("Company Orientation", "Recruitment Basics", "Compliance Training");
            default ->
                    List.of("Company Orientation", "Code of Conduct");
        };
    }
}