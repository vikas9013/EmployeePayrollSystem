package com.vikas.service;

import com.vikas.dto.OnboardingResponseDTO;
import com.vikas.entity.Employee;
import com.vikas.exception.OnboardingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

// CHANGED:
//  1. Added @Slf4j — proper structured logging instead of System.out.println
//  2. Added @RequiredArgsConstructor — removes manual constructor
//  3. Logging at each onboarding step so failures are traceable in production logs
//
// NOTE on OnboardingException package:
//  Rename your existing package from "ExceptionHandler" → "exception" (lowercase)
//  and update the import below accordingly.

@Slf4j
@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final EmailService        emailService;
    private final SlackService        slackService;
    private final TrainingService     trainingService;
    private final PayrollSetupService payrollSetupService;
    private final AIOnboardingService aiOnboardingService;

    /**
     * Runs all onboarding steps in sequence.
     * If any step throws an OnboardingException, execution stops immediately
     * and the exception propagates to GlobalExceptionHandler for a structured error response.
     * The @Transactional on EmployeeService.addEmployeeWithOnboarding()
     * will roll back the saved employee if any step here fails.
     */
    public OnboardingResponseDTO onboard(Employee employee) {
        log.info("Starting onboarding pipeline for employee: {} (id: {})",
                employee.getName(), employee.getId());

        // Step 1 — Email
        log.info("[Step 1/5] Creating work email for: {}", employee.getName());
        String workEmail = emailService.createWorkEmail(employee);
        log.info("[Step 1/5] Work email created: {}", workEmail);

        // Step 2 — Slack
        log.info("[Step 2/5] Sending Slack invite to: {}", workEmail);
        slackService.sendWorkspaceInvite(workEmail, employee.getName());
        log.info("[Step 2/5] Slack invite sent");

        // Step 3 — Training
        log.info("[Step 3/5] Assigning training modules to: {}", employee.getName());
        trainingService.assignTrainingModules(employee);
        log.info("[Step 3/5] Training modules assigned");

        // Step 4 — Payroll
        log.info("[Step 4/5] Setting up payroll for: {}", employee.getName());
        payrollSetupService.setupPayroll(employee);
        log.info("[Step 4/5] Payroll configured");

        // Step 5 — AI welcome message
        log.info("[Step 5/5] Generating AI welcome message for: {}", employee.getName());
        String aiMessage = aiOnboardingService.generateMessage(
                employee.getName(), employee.getDesignation(), null);
        log.info("[Step 5/5] AI welcome message generated");

        log.info("Onboarding pipeline completed successfully for employee id: {}",
                employee.getId());

        return OnboardingResponseDTO.builder()
                .employeeId(employee.getId())
                .employeeName(employee.getName())
                .workEmail(workEmail)
                .slackInviteSent(true)
                .trainingAssigned(true)
                .payrollConfigured(true)
                .message("Onboarding completed successfully for " + employee.getName())
                .aiOnboardingMessage(aiMessage)
                .build();
    }
}