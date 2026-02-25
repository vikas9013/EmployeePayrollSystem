package com.vikas;

import java.util.HashMap;

public class PayrollSystem {

        private final HashMap<Integer, Employee> employeeMap;

        public PayrollSystem(){
            employeeMap = new HashMap<>();
        }

        public void addEmployee(Employee employee) {
            if (employeeMap.containsKey(employee.getId())) {
                throw new IllegalArgumentException("Employee ID " + employee.getId() + " already exists.");
            }
            employeeMap.put(employee.getId(), employee);
        }

        public void removeEmployee(int id){
            employeeMap.remove(id);
        }

        public void displayEmployee(){
            for(Employee employee : employeeMap.values()){
                System.out.println(employee);
            }
        }
    }
