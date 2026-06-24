package com.vikas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response schema containing calculated salary details")
public class SalaryResponseDTO implements Serializable {

    @Schema(description = "Database ID of the employee", example = "1")
    private Long   employeeId;

    @Schema(description = "Name of the employee", example = "Vikas Kumar")
    private String employeeName;

    @Schema(description = "Calculated salary value (Full-time: base salary, Part-time: hours * rate)", example = "75000.00")
    private double salary;
}