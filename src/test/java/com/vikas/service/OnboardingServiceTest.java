package com.vikas.service;

import com.vikas.dto.OnboardingResponseDTO;
import com.vikas.entity.FullTimeEmployee;
import com.vikas.entity.PartTimeEmployee;
import com.vikas.ExceptionHandler.OnboardingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OnboardingServiceTest {

    @Mock
    private EmailService emailService;

    @Mock
    private SlackService slackService;

    @Mock
    private TrainingService trainingService;

    @Mock
    private PayrollSetupService payrollSetupService;

    @Mock
    private AIOnboardingService aiOnboardingService;

    @InjectMocks
    private OnboardingService onboardingService;

    private FullTimeEmployee fullTimeEmployee;
    private PartTimeEmployee partTimeEmployee;

    @BeforeEach
    void setUp() {
        fullTimeEmployee = new FullTimeEmployee("Vikas", "Engineer", 85000);
        fullTimeEmployee.setId(1L);

        partTimeEmployee = new PartTimeEmployee("Rahul", "Intern", 40, 200);
        partTimeEmployee.setId(2L);
    }

    // --- Happy path tests ---

    @Test
    void onboard_FullTimeEmployee_AllStepsExecuted() {
        when(emailService.createWorkEmail(fullTimeEmployee)).thenReturn("vikas@company.com");
        when(aiOnboardingService.generateMessage(anyString(), anyString(), any()))
                .thenReturn("Welcome Vikas!");

        OnboardingResponseDTO result = onboardingService.onboard(fullTimeEmployee);

        assertNotNull(result);
        assertEquals(1L, result.getEmployeeId());
        assertEquals("Vikas", result.getEmployeeName());
        assertEquals("vikas@company.com", result.getWorkEmail());
        assertTrue(result.isSlackInviteSent());
        assertTrue(result.isTrainingAssigned());
        assertTrue(result.isPayrollConfigured());
        assertEquals("Welcome Vikas!", result.getAiOnboardingMessage());
    }

    @Test
    void onboard_PartTimeEmployee_AllStepsExecuted() {
        when(emailService.createWorkEmail(partTimeEmployee)).thenReturn("rahul@company.com");
        when(aiOnboardingService.generateMessage(anyString(), anyString(), any()))
                .thenReturn("Welcome Rahul!");

        OnboardingResponseDTO result = onboardingService.onboard(partTimeEmployee);

        assertNotNull(result);
        assertEquals("Rahul", result.getEmployeeName());
        assertEquals("rahul@company.com", result.getWorkEmail());
    }

    @Test
    void onboard_MessageContainsEmployeeName() {
        when(emailService.createWorkEmail(fullTimeEmployee)).thenReturn("vikas@company.com");
        when(aiOnboardingService.generateMessage(anyString(), anyString(), any()))
                .thenReturn("Welcome!");

        OnboardingResponseDTO result = onboardingService.onboard(fullTimeEmployee);

        assertTrue(result.getMessage().contains("Vikas"));
    }

    @Test
    void onboard_AllServicesCalled() {
        when(emailService.createWorkEmail(fullTimeEmployee)).thenReturn("vikas@company.com");
        when(aiOnboardingService.generateMessage(anyString(), anyString(), any()))
                .thenReturn("Welcome!");

        onboardingService.onboard(fullTimeEmployee);

        // Verify all 5 steps were called exactly once
        verify(emailService, times(1)).createWorkEmail(fullTimeEmployee);
        verify(slackService, times(1)).sendWorkspaceInvite("vikas@company.com", "Vikas");
        verify(trainingService, times(1)).assignTrainingModules(fullTimeEmployee);
        verify(payrollSetupService, times(1)).setupPayroll(fullTimeEmployee);
        verify(aiOnboardingService, times(1)).generateMessage("Vikas", "Engineer", null);
    }

    // --- Failure path tests ---

    @Test
    void onboard_EmailFails_ThrowsOnboardingException() {
        when(emailService.createWorkEmail(fullTimeEmployee))
                .thenThrow(new OnboardingException("EMAIL_CREATION", "Email failed", new RuntimeException()));

        assertThrows(OnboardingException.class, () -> onboardingService.onboard(fullTimeEmployee));

        // Slack should NOT be called if email fails
        verify(slackService, never()).sendWorkspaceInvite(anyString(), anyString());
    }

    @Test
    void onboard_SlackFails_ThrowsOnboardingException() {
        when(emailService.createWorkEmail(fullTimeEmployee)).thenReturn("vikas@company.com");
        doThrow(new OnboardingException("SLACK_INVITE", "Slack failed", new RuntimeException()))
                .when(slackService).sendWorkspaceInvite(anyString(), anyString());

        assertThrows(OnboardingException.class, () -> onboardingService.onboard(fullTimeEmployee));

        // Training should NOT be called if Slack fails
        verify(trainingService, never()).assignTrainingModules(any());
    }

    @Test
    void onboard_TrainingFails_ThrowsOnboardingException() {
        when(emailService.createWorkEmail(fullTimeEmployee)).thenReturn("vikas@company.com");
        doThrow(new OnboardingException("TRAINING_ASSIGNMENT", "Training failed", new RuntimeException()))
                .when(trainingService).assignTrainingModules(any());

        assertThrows(OnboardingException.class, () -> onboardingService.onboard(fullTimeEmployee));

        // Payroll should NOT be called if training fails
        verify(payrollSetupService, never()).setupPayroll(any());
    }

    @Test
    void onboard_PayrollFails_ThrowsOnboardingException() {
        when(emailService.createWorkEmail(fullTimeEmployee)).thenReturn("vikas@company.com");
        doThrow(new OnboardingException("PAYROLL_SETUP", "Payroll failed", new RuntimeException()))
                .when(payrollSetupService).setupPayroll(any());

        assertThrows(OnboardingException.class, () -> onboardingService.onboard(fullTimeEmployee));
    }
}
