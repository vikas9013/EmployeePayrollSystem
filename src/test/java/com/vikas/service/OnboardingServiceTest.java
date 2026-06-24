package com.vikas.service;

import com.vikas.event.OnboardingEvent;
import com.vikas.entity.FullTimeEmployee;
import com.vikas.entity.PartTimeEmployee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Mock
    private EmployeeService employeeService;

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

    @Test
    void handleOnboardingEvent_FullTimeEmployee_AllStepsExecuted() {
        when(emailService.createWorkEmail(fullTimeEmployee)).thenReturn("vikas@company.com");
        when(aiOnboardingService.generateMessage(anyString(), anyString(), any()))
                .thenReturn("Welcome Vikas!");

        OnboardingEvent event = new OnboardingEvent(this, fullTimeEmployee);
        onboardingService.handleOnboardingEvent(event);

        verify(emailService).createWorkEmail(fullTimeEmployee);
        verify(slackService).sendWorkspaceInvite("vikas@company.com", "Vikas");
        verify(trainingService).assignTrainingModules(fullTimeEmployee);
        verify(payrollSetupService).setupPayroll(fullTimeEmployee);
        verify(aiOnboardingService).generateMessage("Vikas", "Engineer", null);
        verify(employeeService).saveOnboardingProgress(fullTimeEmployee);
    }

    @Test
    void handleOnboardingEvent_PartTimeEmployee_AllStepsExecuted() {
        when(emailService.createWorkEmail(partTimeEmployee)).thenReturn("rahul@company.com");
        when(aiOnboardingService.generateMessage(anyString(), anyString(), any()))
                .thenReturn("Welcome Rahul!");

        OnboardingEvent event = new OnboardingEvent(this, partTimeEmployee);
        onboardingService.handleOnboardingEvent(event);

        verify(emailService).createWorkEmail(partTimeEmployee);
        verify(slackService).sendWorkspaceInvite("rahul@company.com", "Rahul");
        verify(trainingService).assignTrainingModules(partTimeEmployee);
        verify(payrollSetupService).setupPayroll(partTimeEmployee);
        verify(aiOnboardingService).generateMessage("Rahul", "Intern", null);
        verify(employeeService).saveOnboardingProgress(partTimeEmployee);
    }

    @Test
    void handleOnboardingEvent_ExceptionHandledGracefully() {
        when(emailService.createWorkEmail(fullTimeEmployee)).thenThrow(new RuntimeException("Email failed"));

        OnboardingEvent event = new OnboardingEvent(this, fullTimeEmployee);
        onboardingService.handleOnboardingEvent(event);

        verify(emailService).createWorkEmail(fullTimeEmployee);
        verifyNoInteractions(slackService);
        verifyNoInteractions(trainingService);
        verifyNoInteractions(payrollSetupService);
        verifyNoInteractions(aiOnboardingService);
    }
}