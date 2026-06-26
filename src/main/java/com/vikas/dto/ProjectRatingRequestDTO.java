package com.vikas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for adding a project rating")
public class ProjectRatingRequestDTO {

    @NotNull(message = "Employee ID cannot be null")
    @Schema(description = "ID of the employee being rated", example = "1")
    private Long employeeId;

    @NotBlank(message = "Project name cannot be empty")
    @Schema(description = "Name of the project", example = "Payroll System Modernization")
    private String projectName;

    @NotNull(message = "Score cannot be null")
    @Min(value = 1, message = "Score must be at least 1")
    @Max(value = 5, message = "Score must not exceed 5")
    @Schema(description = "Rating score from 1 to 5", example = "4")
    private Integer score;

    @Schema(description = "Optional feedback comments", example = "Great performance, delivered on time.")
    private String feedback;
}
