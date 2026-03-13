package com.vikas.dto;

public class EmployeeResponseDTO {

    private Long id;
    private String name;
    private String designation;
    private String type;
    private double salary;

    public EmployeeResponseDTO() {}

    public EmployeeResponseDTO(Long id, String name,String designation, String type, double salary) {
        this.id     = id;
        this.name   = name;
        this.designation = designation;
        this.type   = type;
        this.salary = salary;
    }

    public String getDesignation() {
        return designation;
    }
    public void setDesignation(String designation) {
        this.designation = designation;
    }
    public Long getId()        { return id; }
    public String getName()    { return name; }
    public String getType()    { return type; }
    public double getSalary()  { return salary; }
    public void setId(Long id)         { this.id = id; }
    public void setName(String name)   { this.name = name; }
    public void setType(String type)   { this.type = type; }
    public void setSalary(double salary) { this.salary = salary; }
}