package com.vikas.dto;

import com.vikas.enums.EmployeeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class EmployeeRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Designation is required")
    private String designation;

    @NotNull(message = "Type is required (FULLTIME or PARTTIME)")
    private EmployeeType type;

    // For FullTimeEmployee
    @PositiveOrZero(message = "Monthly salary cannot be negative")
    private double monthlySalary;

    // For PartTimeEmployee
    @PositiveOrZero(message = "Hours worked cannot be negative")
    private int hoursWorked;

    @PositiveOrZero(message = "Hourly rate cannot be negative")
    private double hourlyRate;


    public String getDesignation()             {return designation;
    }
    public void setDesignation(String designation) {
        this.designation = designation;
    }
    public String getName()                    { return name; }
    public void setName(String name)           { this.name = name; }
    public EmployeeType getType()              { return type; }
    public void setType(EmployeeType type)     { this.type = type; }
    public double getMonthlySalary()           { return monthlySalary; }
    public void setMonthlySalary(double s)     { this.monthlySalary = s; }
    public int getHoursWorked()                { return hoursWorked; }
    public void setHoursWorked(int h)          { this.hoursWorked = h; }
    public double getHourlyRate()              { return hourlyRate; }
    public void setHourlyRate(double r)        { this.hourlyRate = r; }
}