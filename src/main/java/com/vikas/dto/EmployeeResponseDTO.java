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
@Schema(description = "Response schema containing comprehensive employee and onboarding details")
public class EmployeeResponseDTO implements Serializable {

    @Schema(description = "Unique database ID of the employee", example = "1")
    private Long   id;

    @Schema(description = "Full name of the employee", example = "Vikas Kumar")
    private String name;

    @Schema(description = "Job designation/role", example = "Senior Java Developer")
    private String designation;

    @Schema(description = "Employment type", example = "FULLTIME")
    private String type;

    @Schema(description = "Calculated salary value", example = "75000.00")
    private double salary;

    @Schema(description = "Generated corporate email address", example = "vikaskumar@company.com")
    private String workEmail;

    @Schema(description = "Flag indicating if Slack workspace invite was sent", example = "true")
    private boolean slackInviteSent;

    @Schema(description = "Flag indicating if mandatory training was assigned", example = "true")
    private boolean trainingAssigned;

    @Schema(description = "Flag indicating if bank payroll accounts are configured", example = "true")
    private boolean payrollConfigured;

    @Schema(description = "AI-generated personalized onboarding welcome greeting", example = "Welcome Vikas Kumar to the team! ...")
    private String aiOnboardingMessage;
}