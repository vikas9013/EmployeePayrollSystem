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

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request schema for creating or updating an employee")
public class EmployeeRequestDTO {

    @NotBlank(message = "Name is required")
    @Schema(description = "Full name of the employee", example = "Vikas Kumar")
    private String name;

    @NotBlank(message = "Designation is required")
    @Schema(description = "Job designation/role of the employee", example = "Senior Java Developer")
    private String designation;

    @NotNull(message = "Type is required (FULLTIME or PARTTIME)")
    @Schema(description = "Employment type classification", example = "FULLTIME")
    private EmployeeType type;

    @PositiveOrZero(message = "Monthly salary cannot be negative")
    @Schema(description = "Monthly base salary (applicable only for FULLTIME employees)", example = "75000.00")
    private double monthlySalary;

    @PositiveOrZero(message = "Hours worked cannot be negative")
    @Schema(description = "Number of hours worked (applicable only for PARTTIME employees)", example = "40")
    private int hoursWorked;

    @PositiveOrZero(message = "Hourly rate cannot be negative")
    @Schema(description = "Hourly rate for calculations (applicable only for PARTTIME employees)", example = "250.00")
    private double hourlyRate;
}