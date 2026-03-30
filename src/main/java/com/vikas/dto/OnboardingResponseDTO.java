package com.vikas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// CHANGED: Replaced hand-written getters/setters with Lombok @Data

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingResponseDTO {

    private Long    employeeId;
    private String  employeeName;
    private String  workEmail;
    private boolean slackInviteSent;
    private boolean trainingAssigned;
    private boolean payrollConfigured;
    private String  message;
    private String  aiOnboardingMessage;
}