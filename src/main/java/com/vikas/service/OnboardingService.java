package com.vikas.service;

import com.vikas.entity.Employee;
import com.vikas.dto.OnboardingResponseDTO;
import com.vikas.ExceptionHandler.OnboardingException;
import org.springframework.stereotype.Service;

@Service
public class OnboardingService {

    private final EmailService        emailService;
    private final SlackService        slackService;
    private final TrainingService     trainingService;
    private final PayrollSetupService payrollSetupService;
    private final AIOnboardingService aiOnboardingService;

    public OnboardingService(EmailService emailService,
                             SlackService slackService,
                             TrainingService trainingService,
                             PayrollSetupService payrollSetupService,
                             AIOnboardingService aiOnboardingService) {
        this.emailService        = emailService;
        this.slackService        = slackService;
        this.trainingService     = trainingService;
        this.payrollSetupService = payrollSetupService;
        this.aiOnboardingService = aiOnboardingService;
    }

    /**
     * Runs all onboarding steps sequentially.
     * If any step throws an OnboardingException, execution stops immediately
     * and the exception propagates to GlobalExceptionHandler for a structured error response.
     *
     * Steps (in order):
     *   1. Create work email
     *   2. Send Slack workspace invite
     *   3. Assign training modules
     *   4. Configure payroll
     *
     * @param employee the newly persisted Employee entity
     * @return OnboardingResponseDTO with status of each completed step
     * @throws OnboardingException if any step fails
     */
    public OnboardingResponseDTO onboard(Employee employee) {

        // Step 1 — Email
        // Step 1 — Email
        String workEmail = emailService.createWorkEmail(employee);

        // Step 2 — Slack
        slackService.sendWorkspaceInvite(workEmail, employee.getName());

        // Step 3 — Training
        trainingService.assignTrainingModules(employee);

        // Step 4 — Payroll
        payrollSetupService.setupPayroll(employee);

        // Step 5 — AI Onboarding Message (Anthropic Claude)   // ADD FROM HERE
        String aiMessage = aiOnboardingService.generateMessage(
                employee.getName(),
                employee.getDesignation(),
                null
        );                                                      // TO HERE

        return new OnboardingResponseDTO(
                employee.getId(),
                employee.getName(),
                workEmail,
                true,
                true,
                true,
                "Onboarding completed successfully for " + employee.getName(),
                aiMessage   // ADD THIS ARGUMENT
        );
    }
}