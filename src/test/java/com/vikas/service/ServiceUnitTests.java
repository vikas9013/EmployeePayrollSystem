package com.vikas.service;

import com.vikas.entity.FullTimeEmployee;
import com.vikas.entity.PartTimeEmployee;
import com.vikas.ExceptionHandler.OnboardingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailServiceTest {

    private EmailService emailService;
    private FullTimeEmployee employee;

    @BeforeEach
    void setUp() {
        emailService = new EmailService();
        employee = new FullTimeEmployee("Vikas Singh", "Engineer", 85000);
    }

    @Test
    void createWorkEmail_SimpleNameFormatsCorrectly() {
        FullTimeEmployee emp = new FullTimeEmployee("Vikas", "Engineer", 85000);
        String email = emailService.createWorkEmail(emp);
        assertEquals("vikas@company.com", email);
    }

    @Test
    void createWorkEmail_SpacesReplacedWithDots() {
        String email = emailService.createWorkEmail(employee);
        assertEquals("vikas.singh@company.com", email);
    }

    @Test
    void createWorkEmail_UpperCaseConvertedToLowerCase() {
        FullTimeEmployee emp = new FullTimeEmployee("VIKAS SINGH", "Engineer", 85000);
        String email = emailService.createWorkEmail(emp);
        assertEquals("vikas.singh@company.com", email);
    }

    @Test
    void createWorkEmail_EndsWithCompanyDomain() {
        String email = emailService.createWorkEmail(employee);
        assertTrue(email.endsWith("@company.com"));
    }

    @Test
    void createWorkEmail_ReturnsNonNullEmail() {
        String email = emailService.createWorkEmail(employee);
        assertNotNull(email);
        assertFalse(email.isBlank());
    }
}

class SlackServiceTest {

    private SlackService slackService;

    @BeforeEach
    void setUp() {
        slackService = new SlackService();
    }

    @Test
    void sendWorkspaceInvite_DoesNotThrow() {
        assertDoesNotThrow(() ->
                slackService.sendWorkspaceInvite("vikas@company.com", "Vikas")
        );
    }

    @Test
    void sendWorkspaceInvite_WithValidInputs_Succeeds() {
        // Should complete without any exception
        assertDoesNotThrow(() ->
                slackService.sendWorkspaceInvite("rahul@company.com", "Rahul")
        );
    }
}

class TrainingServiceTest {

    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        trainingService = new TrainingService();
    }

    @Test
    void assignTrainingModules_Engineer_DoesNotThrow() {
        FullTimeEmployee emp = new FullTimeEmployee("Vikas", "engineer", 85000);
        assertDoesNotThrow(() -> trainingService.assignTrainingModules(emp));
    }

    @Test
    void assignTrainingModules_Developer_DoesNotThrow() {
        FullTimeEmployee emp = new FullTimeEmployee("Vikas", "developer", 85000);
        assertDoesNotThrow(() -> trainingService.assignTrainingModules(emp));
    }

    @Test
    void assignTrainingModules_Manager_DoesNotThrow() {
        FullTimeEmployee emp = new FullTimeEmployee("Vikas", "manager", 85000);
        assertDoesNotThrow(() -> trainingService.assignTrainingModules(emp));
    }

    @Test
    void assignTrainingModules_HR_DoesNotThrow() {
        FullTimeEmployee emp = new FullTimeEmployee("Vikas", "hr", 85000);
        assertDoesNotThrow(() -> trainingService.assignTrainingModules(emp));
    }

    @Test
    void assignTrainingModules_UnknownDesignation_DoesNotThrow() {
        FullTimeEmployee emp = new FullTimeEmployee("Vikas", "Intern", 85000);
        assertDoesNotThrow(() -> trainingService.assignTrainingModules(emp));
    }

    @Test
    void assignTrainingModules_NullDesignation_DoesNotThrow() {
        FullTimeEmployee emp = new FullTimeEmployee("Vikas", null, 85000);
        assertDoesNotThrow(() -> trainingService.assignTrainingModules(emp));
    }
}

class PayrollSetupServiceTest {

    private PayrollSetupService payrollSetupService;

    @BeforeEach
    void setUp() {
        payrollSetupService = new PayrollSetupService();
    }

    @Test
    void setupPayroll_FullTimeEmployee_DoesNotThrow() {
        FullTimeEmployee emp = new FullTimeEmployee("Vikas", "Engineer", 85000);
        assertDoesNotThrow(() -> payrollSetupService.setupPayroll(emp));
    }

    @Test
    void setupPayroll_PartTimeEmployee_DoesNotThrow() {
        PartTimeEmployee emp = new PartTimeEmployee("Rahul", "Intern", 40, 200);
        assertDoesNotThrow(() -> payrollSetupService.setupPayroll(emp));
    }

    @Test
    void setupPayroll_FullTimeEmployee_WithZeroSalary_DoesNotThrow() {
        FullTimeEmployee emp = new FullTimeEmployee("Vikas", "Engineer", 0);
        assertDoesNotThrow(() -> payrollSetupService.setupPayroll(emp));
    }

    @Test
    void setupPayroll_PartTimeEmployee_WithZeroHours_DoesNotThrow() {
        PartTimeEmployee emp = new PartTimeEmployee("Rahul", "Intern", 0, 200);
        assertDoesNotThrow(() -> payrollSetupService.setupPayroll(emp));
    }
}