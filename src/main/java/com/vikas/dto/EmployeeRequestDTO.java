package com.vikas.dto;

import com.vikas.enums.EmployeeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// CHANGED: Replaced hand-written getters/setters with Lombok @Data
//          Added @Builder for easy test construction

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Designation is required")
    private String designation;

    @NotNull(message = "Type is required (FULLTIME or PARTTIME)")
    private EmployeeType type;

    @PositiveOrZero(message = "Monthly salary cannot be negative")
    private double monthlySalary;

    @PositiveOrZero(message = "Hours worked cannot be negative")
    private int hoursWorked;

    @PositiveOrZero(message = "Hourly rate cannot be negative")
    private double hourlyRate;
}