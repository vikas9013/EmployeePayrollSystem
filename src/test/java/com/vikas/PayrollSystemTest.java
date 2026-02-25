package com.vikas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PayrollSystemTest {

    // -------------------------------------------------------
    // We'll reuse these objects across multiple tests
    // -------------------------------------------------------
    private FullTimeEmployee fullTimeEmployee;
    private PartTimeEmployee partTimeEmployee;
    private PayrollSystem payrollSystem;

    @BeforeEach
    void setUp() {
        // This runs BEFORE every single test automatically
        fullTimeEmployee = new FullTimeEmployee("Vikas", 1001, 85000);
        partTimeEmployee = new PartTimeEmployee("Rahul", 1002, 40, 200);
        payrollSystem    = new PayrollSystem();
    }

    // -------------------------------------------------------
    // 1. Salary calculation tests
    // -------------------------------------------------------

    @Test
    void fullTimeEmployee_SalaryEqualsFixedMonthlySalary() {
        // Full-time salary should simply be the monthly salary
        assertEquals(85000, fullTimeEmployee.calculateSalary(),
                "Full-time salary should equal the fixed monthly salary");
    }

    @Test
    void partTimeEmployee_SalaryEqualsHoursWorkedMultipliedByRate() {
        // 40 hours × 200 rate = 8000
        assertEquals(8000, partTimeEmployee.calculateSalary(),
                "Part-time salary should be hoursWorked * hourlyRate");
    }

    @Test
    void partTimeEmployee_ZeroHours_SalaryIsZero() {
        // Edge case: if no hours worked, salary must be 0
        PartTimeEmployee zeroHours = new PartTimeEmployee("Test", 9999, 0, 200);
        assertEquals(0, zeroHours.calculateSalary(),
                "Salary should be 0 when no hours are worked");
    }

    // -------------------------------------------------------
    // 2. Employee name and ID tests
    // -------------------------------------------------------

    @Test
    void employee_GetNameReturnsCorrectName() {
        assertEquals("Vikas", fullTimeEmployee.getName());
    }

    @Test
    void employee_GetIdReturnsCorrectId() {
        assertEquals(1001, fullTimeEmployee.getId());
    }

    // -------------------------------------------------------
    // 3. toString() tests
    // -------------------------------------------------------

    @Test
    void fullTimeEmployee_ToStringContainsClassName() {
        // Should say "FullTimeEmployee[..." not "com.vikas.Employee[..."
        assertTrue(fullTimeEmployee.toString().startsWith("FullTimeEmployee"),
                "toString should start with the actual class name");
    }

    @Test
    void partTimeEmployee_ToStringContainsClassName() {
        assertTrue(partTimeEmployee.toString().startsWith("PartTimeEmployee"),
                "toString should start with the actual class name");
    }

    // -------------------------------------------------------
    // 4. PayrollSystem — add and display tests
    // -------------------------------------------------------

    @Test
    void payrollSystem_AddEmployee_EmployeeIsAdded() {
        payrollSystem.addEmployee(fullTimeEmployee);
        // If no exception thrown and system works, employee was added
        // (We verify indirectly — a more advanced test would check size)
        assertDoesNotThrow(() -> payrollSystem.displayEmployee());
    }

    @Test
    void payrollSystem_AddMultipleEmployees_NoErrors() {
        assertDoesNotThrow(() -> {
            payrollSystem.addEmployee(fullTimeEmployee);
            payrollSystem.addEmployee(partTimeEmployee);
            payrollSystem.displayEmployee();
        });
    }

    // -------------------------------------------------------
    // 5. PayrollSystem — remove tests
    // -------------------------------------------------------

    @Test
    void payrollSystem_RemoveEmployee_NoErrors() {
        payrollSystem.addEmployee(fullTimeEmployee);
        assertDoesNotThrow(() -> payrollSystem.removeEmployee(1001));
    }

    @Test
    void payrollSystem_RemoveNonExistentEmployee_NoErrors() {
        // Removing an ID that doesn't exist should not crash
        assertDoesNotThrow(() -> payrollSystem.removeEmployee(9999));
    }

}
