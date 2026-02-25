package com.vikas;

public class Main {
    public static void main(String[] args) {
        PayrollSystem payrollSystem = new PayrollSystem();
        FullTimeEmployee emp1= new FullTimeEmployee("Vikas",1001,85000);
        PartTimeEmployee emp2 = new PartTimeEmployee("rahul",1002,40,200);

        payrollSystem.addEmployee(emp1);
        payrollSystem.addEmployee(emp2);

        System.out.println("Employee details: ");
        payrollSystem.displayEmployee();
        System.out.println("Removing Employees");
        payrollSystem.removeEmployee(1002);
        System.out.print("Remaining Employees details: ");
        payrollSystem.displayEmployee();

        }
    }