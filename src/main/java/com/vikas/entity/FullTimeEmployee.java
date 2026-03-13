package com.vikas.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "fulltime_employees")
public class FullTimeEmployee extends Employee {

    @PositiveOrZero(message = "Monthly salary cannot be negative")
    private double monthlySalary;

    public FullTimeEmployee() {}

    public FullTimeEmployee(String name, String designation,double monthlySalary) {
        super(name,designation);
        this.monthlySalary = monthlySalary;
    }

    public double getMonthlySalary()            { return monthlySalary; }
    public void setMonthlySalary(double salary) { this.monthlySalary = salary; }

    @Override
    public double calculateSalary() {
        return monthlySalary;
    }
}