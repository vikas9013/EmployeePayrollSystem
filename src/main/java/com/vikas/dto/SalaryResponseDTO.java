package com.vikas.dto;

public class SalaryResponseDTO {

    private Long employeeId;
    private String employeeName;
    private double salary;

    public SalaryResponseDTO() {}

    public SalaryResponseDTO(Long employeeId, String employeeName, double salary) {
        this.employeeId   = employeeId;
        this.employeeName = employeeName;
        this.salary       = salary;
    }

    public Long getEmployeeId()           { return employeeId; }
    public String getEmployeeName()       { return employeeName; }
    public double getSalary()             { return salary; }
    public void setEmployeeId(Long id)    { this.employeeId = id; }
    public void setEmployeeName(String n) { this.employeeName = n; }
    public void setSalary(double salary)  { this.salary = salary; }
}
