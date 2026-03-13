package com.vikas.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "parttime_employees")
public class PartTimeEmployee extends Employee {

    @PositiveOrZero(message = "Hours worked cannot be negative")
    private int hoursWorked;

    @PositiveOrZero(message = "Hourly rate cannot be negative")
    private double hourlyRate;

    public PartTimeEmployee() {}

    public PartTimeEmployee(String name, String designation,int hoursWorked, double hourlyRate) {
        super(name,designation);
        this.hoursWorked = hoursWorked;
        this.hourlyRate  = hourlyRate;
    }

    public int getHoursWorked()            { return hoursWorked; }
    public double getHourlyRate()          { return hourlyRate; }
    public void setHoursWorked(int hours)  { this.hoursWorked = hours; }
    public void setHourlyRate(double rate) { this.hourlyRate = rate; }

    @Override
    public double calculateSalary() {
        return hoursWorked * hourlyRate;
    }
}