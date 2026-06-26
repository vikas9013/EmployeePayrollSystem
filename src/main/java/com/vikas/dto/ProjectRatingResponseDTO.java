package com.vikas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response payload containing project rating details")
public class ProjectRatingResponseDTO implements Serializable {

    @Schema(description = "Unique ID of the rating", example = "1")
    private Long id;

    @Schema(description = "ID of the employee being rated", example = "1")
    private Long employeeId;

    @Schema(description = "Name of the project", example = "Payroll System Modernization")
    private String projectName;

    @Schema(description = "Rating score from 1 to 5", example = "4")
    private Integer score;

    @Schema(description = "Optional feedback comments", example = "Great performance, delivered on time.")
    private String feedback;

    @Schema(description = "Timestamp when the rating was created", example = "2023-10-25T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Username of the admin who created the rating", example = "admin_user")
    private String createdBy;
}
