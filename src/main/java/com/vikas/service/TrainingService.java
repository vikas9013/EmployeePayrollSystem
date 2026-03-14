package com.vikas.service;

import com.vikas.entity.Employee;
import com.vikas.ExceptionHandler.OnboardingException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingService {

    /**
     * Simulates assigning training modules to the new hire based on their designation.
     * In production, replace this body with a real call to your LMS
     * (e.g. Cornerstone, Docebo, TalentLMS API).
     *
     * @param employee the newly saved employee
     * @throws OnboardingException if module assignment fails
     */
    public void assignTrainingModules(Employee employee) {
        try {
            List<String> modules = getLearningResources(employee.getDesignation());

            // --- Replace with real LMS API call ---
            System.out.println("[TrainingService] Assigned modules to "
                    + employee.getName() + ": " + modules);

        } catch (OnboardingException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new OnboardingException(
                    "TRAINING_ASSIGNMENT",
                    "Failed to assign training modules for: " + employee.getName(),
                    ex
            );
        }
    }

    /**
     * Maps a designation to a list of training module names.
     * Extend this logic to cover all roles in your organisation.
     */
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