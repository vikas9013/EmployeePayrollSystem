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

// ✅ Step 3 - New Swagger imports
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

// ✅ @Tag groups ALL endpoints in this controller under one section in Swagger UI
@Tag(name = "Employee Management", description = "APIs for managing employees, payroll and onboarding")
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }



    // GET ALL EMPLOYEES
    @Operation(
            summary     = "Get all employees",
            description = "Returns a list of all employees (both full-time and part-time)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description  = "Successfully retrieved list of employees",
                    // ✅ content + schema tells Swagger what the response body looks like
                    content      = @Content(schema = @Schema(implementation = EmployeeResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description  = "Internal server error",
                    content      = @Content
            )
    })
    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> getAllEmployees() {
        return ResponseEntity.ok(service.getAllEmployees());
    }

    // GET EMPLOYEE BY ID

    @Operation(
            summary     = "Get employee by ID",
            description = "Returns a single employee's details by their unique ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description  = "Employee found successfully",
                    content      = @Content(schema = @Schema(implementation = EmployeeResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description  = "Employee not found with the given ID",
                    content      = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description  = "Invalid ID format supplied",
                    content      = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(
            // ✅ @Parameter describes the path variable shown in Swagger UI input box
            @Parameter(
                    description = "Unique ID of the employee",
                    required    = true,
                    example     = "1"
            )
            @PathVariable Long id) {
        return ResponseEntity.ok(service.getEmployeeById(id));
    }



    // GET EMPLOYEE SALARY

    @Operation(
            summary     = "Get salary of an employee",
            description = "Returns the calculated salary for a given employee ID. "
                    + "Full-time employees return monthly salary; part-time return hours x rate."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description  = "Salary calculated and returned successfully",
                    content      = @Content(schema = @Schema(implementation = SalaryResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description  = "Employee not found with the given ID",
                    content      = @Content
            )
    })
    @GetMapping("/{id}/salary")
    public ResponseEntity<SalaryResponseDTO> getEmployeeSalary(
            @Parameter(
                    description = "Unique ID of the employee",
                    required    = true,
                    example     = "1"
            )
            @PathVariable Long id) {
        return ResponseEntity.ok(service.getEmployeeSalary(id));
    }


    // ONBOARD NEW EMPLOYEE (POST)

    @Operation(
            summary     = "Onboard a new employee",
            description = "Creates a new employee record and triggers the full onboarding workflow: "
                    + "generates work email, sends Slack invite, assigns training modules, "
                    + "configures payroll, and generates an AI welcome message."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description  = "Employee onboarded successfully",
                    content      = @Content(schema = @Schema(implementation = OnboardingResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description  = "Validation failed - check request body fields",
                    content      = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description  = "Onboarding process failed internally",
                    content      = @Content
            )
    })
    @PostMapping("/onboard")
    public ResponseEntity<OnboardingResponseDTO> onboardEmployee(
            @Valid @RequestBody EmployeeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addEmployeeWithOnboarding(dto));
    }


    // UPDATE EMPLOYEE (PUT)

    @Operation(
            summary     = "Update employee details",
            description = "Updates an existing employee's information by ID. "
                    + "Provide all fields - partial updates are not supported."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description  = "Employee updated successfully",
                    content      = @Content(schema = @Schema(implementation = EmployeeResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description  = "Employee not found with the given ID",
                    content      = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description  = "Validation failed - check request body fields",
                    content      = @Content
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(
            @Parameter(
                    description = "Unique ID of the employee to update",
                    required    = true,
                    example     = "1"
            )
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequestDTO dto) {
        return ResponseEntity.ok(service.updateEmployee(id, dto));
    }


    // DELETE EMPLOYEE

    @Operation(
            summary     = "Remove an employee",
            description = "Permanently deletes an employee record from the system by ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description  = "Employee removed successfully",
                    content      = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description  = "Employee not found with the given ID",
                    content      = @Content
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeEmployee(
            @Parameter(
                    description = "Unique ID of the employee to delete",
                    required    = true,
                    example     = "1"
            )
            @PathVariable Long id) {
        service.removeEmployee(id);
        return ResponseEntity.ok("Employee ID " + id + " removed successfully.");
    }
}