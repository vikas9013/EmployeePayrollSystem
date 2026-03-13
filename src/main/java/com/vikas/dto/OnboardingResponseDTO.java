package com.vikas.dto;

public class OnboardingResponseDTO {

    private Long employeeId;
    private String employeeName;
    private String workEmail;
    private boolean slackInviteSent;
    private boolean trainingAssigned;
    private boolean payrollConfigured;
    private String message;

    public OnboardingResponseDTO() {}

    public OnboardingResponseDTO(Long employeeId, String employeeName,
                                 String workEmail, boolean slackInviteSent,
                                 boolean trainingAssigned, boolean payrollConfigured,
                                 String message) {
        this.employeeId       = employeeId;
        this.employeeName     = employeeName;
        this.workEmail        = workEmail;
        this.slackInviteSent  = slackInviteSent;
        this.trainingAssigned = trainingAssigned;
        this.payrollConfigured = payrollConfigured;
        this.message          = message;
    }

    public Long getEmployeeId()              { return employeeId; }
    public String getEmployeeName()          { return employeeName; }
    public String getWorkEmail()             { return workEmail; }
    public boolean isSlackInviteSent()       { return slackInviteSent; }
    public boolean isTrainingAssigned()      { return trainingAssigned; }
    public boolean isPayrollConfigured()     { return payrollConfigured; }
    public String getMessage()               { return message; }

    public void setEmployeeId(Long employeeId)              { this.employeeId = employeeId; }
    public void setEmployeeName(String employeeName)        { this.employeeName = employeeName; }
    public void setWorkEmail(String workEmail)              { this.workEmail = workEmail; }
    public void setSlackInviteSent(boolean slackInviteSent) { this.slackInviteSent = slackInviteSent; }
    public void setTrainingAssigned(boolean trainingAssigned) { this.trainingAssigned = trainingAssigned; }
    public void setPayrollConfigured(boolean payrollConfigured) { this.payrollConfigured = payrollConfigured; }
    public void setMessage(String message)                  { this.message = message; }
}