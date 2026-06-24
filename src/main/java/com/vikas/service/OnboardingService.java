package com.vikas.service;

import com.vikas.event.OnboardingEvent;
import com.vikas.entity.Employee;
import com.vikas.exception.OnboardingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final EmailService        emailService;
    private final SlackService        slackService;
    private final TrainingService     trainingService;
    private final PayrollSetupService payrollSetupService;
    private final AIOnboardingService aiOnboardingService;
    private final EmployeeService     employeeService;

    @Async
    @TransactionalEventListener(phase = org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT)
    public void handleOnboardingEvent(OnboardingEvent event) {
        Employee employee = event.getEmployee();
        log.info("Starting async onboarding pipeline for employee: {} (id: {})",
                employee.getName(), employee.getId());

        try {
            // Step 1 — Email
            log.info("[Step 1/5] Creating work email for: {}", employee.getName());
            String workEmail = emailService.createWorkEmail(employee);
            employee.setWorkEmail(workEmail);
            log.info("[Step 1/5] Work email created: {}", workEmail);

            // Step 2 — Slack
            log.info("[Step 2/5] Sending Slack invite to: {}", workEmail);
            slackService.sendWorkspaceInvite(workEmail, employee.getName());
            employee.setSlackInviteSent(true);
            log.info("[Step 2/5] Slack invite sent");

            // Step 3 — Training
            log.info("[Step 3/5] Assigning training modules to: {}", employee.getName());
            trainingService.assignTrainingModules(employee);
            employee.setTrainingAssigned(true);
            log.info("[Step 3/5] Training modules assigned");

            // Step 4 — Payroll
            log.info("[Step 4/5] Setting up payroll for: {}", employee.getName());
            payrollSetupService.setupPayroll(employee);
            employee.setPayrollConfigured(true);
            log.info("[Step 4/5] Payroll configured");

            // Step 5 — AI welcome message
            log.info("[Step 5/5] Generating AI welcome message for: {}", employee.getName());
            String aiMessage = aiOnboardingService.generateMessage(
                    employee.getName(), employee.getDesignation(), null);
            employee.setAiOnboardingMessage(aiMessage);
            log.info("[Step 5/5] AI welcome message generated: {}", aiMessage);

            // Persist changes and invalidate cache
            employeeService.saveOnboardingProgress(employee);

            log.info("Async onboarding pipeline completed successfully for employee id: {}",
                    employee.getId());
        } catch (Exception e) {
            log.error("Async onboarding failed for employee id: {}", employee.getId(), e);
        }
    }
}