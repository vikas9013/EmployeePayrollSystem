package com.vikas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// CHANGED: Replaced hand-written getters/setters with Lombok @Data

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response schema containing details of the asynchronous onboarding initialization")
public class OnboardingResponseDTO {

    @Schema(description = "Database ID of the onboarded employee", example = "1")
    private Long    employeeId;

    @Schema(description = "Name of the onboarded employee", example = "Vikas Kumar")
    private String  employeeName;

    @Schema(description = "Corporate email address", example = "vikaskumar@company.com")
    private String  workEmail;

    @Schema(description = "Flag indicating if Slack workspace invite was sent", example = "true")
    private boolean slackInviteSent;

    @Schema(description = "Flag indicating if training was assigned", example = "true")
    private boolean trainingAssigned;

    @Schema(description = "Flag indicating if bank payroll accounts are configured", example = "true")
    private boolean payrollConfigured;

    @Schema(description = "Initialization confirmation status message", example = "Onboarding process started asynchronously in the background.")
    private String  message;

    @Schema(description = "AI-generated personalized onboarding welcome greeting (if available synchronous)", example = "Welcome Vikas Kumar to the team! ...")
    private String  aiOnboardingMessage;
}