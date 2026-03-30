package com.vikas.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// CHANGED: Added Lombok @Getter/@Setter/@NoArgsConstructor — removes boilerplate

@Entity
@Table(name = "fulltime_employees")
@Getter
@Setter
@NoArgsConstructor
public class FullTimeEmployee extends Employee {

    @PositiveOrZero(message = "Monthly salary cannot be negative")
    private double monthlySalary;

    public FullTimeEmployee(String name, String designation, double monthlySalary) {
        super(name, designation);
        this.monthlySalary = monthlySalary;
    }

    @Override
    public double calculateSalary() {
        return monthlySalary;
    }
}