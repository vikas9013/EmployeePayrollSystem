package com.vikas;

public abstract class Employee {
    private int id;
    private String name;

    public Employee(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public abstract double calculateSalary();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[name=" + name + ", id=" + id + ", salary=" + calculateSalary() + "]";
    }
}
