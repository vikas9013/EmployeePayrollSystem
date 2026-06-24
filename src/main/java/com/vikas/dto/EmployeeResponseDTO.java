package com.vikas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDTO implements Serializable {

    private Long   id;
    private String name;
    private String designation;
    private String type;
    private double salary;

    private String workEmail;
    private boolean slackInviteSent;
    private boolean trainingAssigned;
    private boolean payrollConfigured;
    private String aiOnboardingMessage;
}