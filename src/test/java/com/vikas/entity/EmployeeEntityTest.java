package com.vikas.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeEntityTest {

    // --- FullTimeEmployee tests ---

    @Test
    void fullTimeEmployee_CalculateSalary_ReturnsMonthly() {
        FullTimeEmployee emp = new FullTimeEmployee("Vikas", "Engineer", 85000);
        assertEquals(85000, emp.calculateSalary());
    }

    @Test
    void fullTimeEmployee_SetMonthlySalary_UpdatesCorrectly() {
        FullTimeEmployee emp = new FullTimeEmployee("Vikas", "Engineer", 85000);
        emp.setMonthlySalary(95000);
        assertEquals(95000, emp.calculateSalary());
    }

    @Test
    void fullTimeEmployee_ZeroSalary_ReturnsZero() {
        FullTimeEmployee emp = new FullTimeEmployee("Vikas", "Engineer", 0);
        assertEquals(0, emp.calculateSalary());
    }

    @Test
    void fullTimeEmployee_GetName_ReturnsCorrectName() {
        FullTimeEmployee emp = new FullTimeEmployee("Vikas", "Engineer", 85000);
        assertEquals("Vikas", emp.getName());
    }

    @Test
    void fullTimeEmployee_GetDesignation_ReturnsCorrectDesignation() {
        FullTimeEmployee emp = new FullTimeEmployee("Vikas", "Engineer", 85000);
        assertEquals("Engineer", emp.getDesignation());
    }

    @Test
    void fullTimeEmployee_ToString_ContainsNameAndSalary() {
        FullTimeEmployee emp = new FullTimeEmployee("Vikas", "Engineer", 85000);
        String str = emp.toString();
        assertTrue(str.contains("Vikas"));
        assertTrue(str.contains("85000"));
    }

    // --- PartTimeEmployee tests ---

    @Test
    void partTimeEmployee_CalculateSalary_HoursTimesRate() {
        PartTimeEmployee emp = new PartTimeEmployee("Rahul", "Intern", 40, 200);
        assertEquals(8000, emp.calculateSalary());
    }

    @Test
    void partTimeEmployee_ZeroHours_SalaryIsZero() {
        PartTimeEmployee emp = new PartTimeEmployee("Rahul", "Intern", 0, 200);
        assertEquals(0, emp.calculateSalary());
    }

    @Test
    void partTimeEmployee_ZeroRate_SalaryIsZero() {
        PartTimeEmployee emp = new PartTimeEmployee("Rahul", "Intern", 40, 0);
        assertEquals(0, emp.calculateSalary());
    }

    @Test
    void partTimeEmployee_UpdateHoursAndRate_RecalculatesCorrectly() {
        PartTimeEmployee emp = new PartTimeEmployee("Rahul", "Intern", 40, 200);
        emp.setHoursWorked(80);
        emp.setHourlyRate(250);
        assertEquals(20000, emp.calculateSalary());
    }

    @Test
    void partTimeEmployee_GetName_ReturnsCorrectName() {
        PartTimeEmployee emp = new PartTimeEmployee("Rahul", "Intern", 40, 200);
        assertEquals("Rahul", emp.getName());
    }

    @Test
    void partTimeEmployee_GetDesignation_ReturnsCorrectDesignation() {
        PartTimeEmployee emp = new PartTimeEmployee("Rahul", "Intern", 40, 200);
        assertEquals("Intern", emp.getDesignation());
    }

    @Test
    void partTimeEmployee_ToString_ContainsNameAndSalary() {
        PartTimeEmployee emp = new PartTimeEmployee("Rahul", "Intern", 40, 200);
        String str = emp.toString();
        assertTrue(str.contains("Rahul"));
        assertTrue(str.contains("8000"));
    }

    // --- Polymorphism test ---

    @Test
    void polymorphism_FullAndPartTimeImplementSameMethod() {
        Employee full = new FullTimeEmployee("Vikas", "Engineer", 85000);
        Employee part = new PartTimeEmployee("Rahul", "Intern", 40, 200);

        // Both are Employee but calculateSalary() behaves differently
        assertEquals(85000, full.calculateSalary());
        assertEquals(8000, part.calculateSalary());
    }
}

