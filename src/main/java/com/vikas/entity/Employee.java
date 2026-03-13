package com.vikas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "employees")
public abstract class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Designation cannot be empty")
    private String designation;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    public Employee() {
    }

    public Employee(String name, String designation) {
        this.name = name;
        this.designation = designation;

        }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
    public Long getId () {
        return id;
    }
    public String getName () {
        return name;
    }
    public void setId (Long id){
        this.id = id;
    }
    public void setName (String name){
        this.name = name;
    }

    public abstract double calculateSalary ();

    @Override
    public String toString () {
        return getClass().getSimpleName()
                + "[id=" + id + ", name=" + name
                + ", salary=" + calculateSalary() + "]";
        }
    }
