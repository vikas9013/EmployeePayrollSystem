package com.vikas.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// CHANGED: Added Lombok @Getter/@Setter/@NoArgsConstructor — removes boilerplate

@Entity
@Table(name = "parttime_employees")
@Getter
@Setter
@NoArgsConstructor
public class PartTimeEmployee extends Employee {

    @PositiveOrZero(message = "Hours worked cannot be negative")
    private int hoursWorked;

    @PositiveOrZero(message = "Hourly rate cannot be negative")
    private double hourlyRate;

    public PartTimeEmployee(String name, String designation, int hoursWorked, double hourlyRate) {
        super(name, designation);
        this.hoursWorked = hoursWorked;
        this.hourlyRate  = hourlyRate;
    }

    @Override
    public double calculateSalary() {
        return hoursWorked * hourlyRate;
    }
}