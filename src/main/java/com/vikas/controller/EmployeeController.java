package com.vikas.controller;

import com.vikas.dto.EmployeeRequestDTO;
import com.vikas.dto.EmployeeResponseDTO;
import com.vikas.dto.SalaryResponseDTO;
import com.vikas.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.vikas.dto.OnboardingResponseDTO;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    // GET all employees
    // URL: GET http://localhost:8080/api/employees
    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> getAllEmployees() {
        return ResponseEntity.ok(service.getAllEmployees());
    }

    // GET employee by ID
    // URL: GET http://localhost:8080/api/employees/1
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getEmployeeById(id));
    }

    // GET salary of an employee
    // URL: GET http://localhost:8080/api/employees/1/salary
    @GetMapping("/{id}/salary")
    public ResponseEntity<SalaryResponseDTO> getEmployeeSalary(@PathVariable Long id) {
        return ResponseEntity.ok(service.getEmployeeSalary(id));
    }

    // POST add new employee
    // URL: POST http://localhost:8080/api/employees
    // Body (full-time):  { "name": "Vikas", "type": "FULLTIME", "monthlySalary": 85000 }
    // Body (part-time):  { "name": "Rahul", "type": "PARTTIME", "hoursWorked": 40, "hourlyRate": 200 }
    @PostMapping("/onboard")
    public ResponseEntity<OnboardingResponseDTO> onboardEmployee(
            @Valid @RequestBody EmployeeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addEmployeeWithOnboarding(dto));
    }

    // PUT update employee
    // URL: PUT http://localhost:8080/api/employees/1
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(@PathVariable Long id,
                                                              @Valid @RequestBody EmployeeRequestDTO dto) {
        return ResponseEntity.ok(service.updateEmployee(id, dto));
    }

    // DELETE remove employee
    // URL: DELETE http://localhost:8080/api/employees/1
    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeEmployee(@PathVariable Long id) {
        service.removeEmployee(id);
        return ResponseEntity.ok("Employee ID " + id + " removed successfully.");
    }
}
