package com.vikas.controller;

import com.vikas.dto.EmployeeRequestDTO;
import com.vikas.dto.EmployeeResponseDTO;
import com.vikas.dto.OnboardingResponseDTO;
import com.vikas.dto.SalaryResponseDTO;
import com.vikas.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

// CHANGED:
//  1. Added @Slf4j — structured logging on every endpoint
//  2. Added @RequiredArgsConstructor — removes manual constructor
//  3. getAllEmployees() now returns Page<> with page/size/sort query params
//  4. Added @SecurityRequirement on class — Swagger UI shows the lock icon and Authorization header
//  5. Added Bucket4j rate limiter on the /onboard endpoint (10 requests/minute)
//     to prevent AI API abuse
//  6. @PreAuthorize added per-method for role-based access control

@Slf4j
@Tag(name = "Employee Management", description = "APIs for managing employees, payroll and onboarding")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;
    private final StringRedisTemplate redisTemplate;

    // ─── GET ALL ────────────────────────────────────────────────────────────

    @Operation(
            summary     = "Get all employees (paginated)",
            description = "Returns a paginated list of all active employees. " +
                    "Use ?page=0&size=20&sort=id,asc to control pagination."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved employees",
                    content = @Content(schema = @Schema(implementation = EmployeeResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized — JWT token missing or invalid",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_HR')")
    public ResponseEntity<Page<EmployeeResponseDTO>> getAllEmployees(
            @RequestParam(defaultValue = "0")   int page,
            @RequestParam(defaultValue = "20")  int size,
            @RequestParam(defaultValue = "id")  String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        log.info("GET /api/employees — page={}, size={}, sort={}", page, size, sortBy);
        return ResponseEntity.ok(service.getAllEmployees(pageable));
    }

    // ─── GET BY ID ──────────────────────────────────────────────────────────

    @Operation(summary = "Get employee by ID", description = "Returns a single employee's details.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee found",
                    content = @Content(schema = @Schema(implementation = EmployeeResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_HR') or (hasAuthority('ROLE_EMPLOYEE') and @securityService.isSelf(authentication, #a0))")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(
            @Parameter(description = "Unique ID of the employee", required = true, example = "1")
            @PathVariable Long id) {
        log.info("GET /api/employees/{}", id);
        return ResponseEntity.ok(service.getEmployeeById(id));
    }

    // ─── GET SALARY ─────────────────────────────────────────────────────────

    @Operation(
            summary     = "Get salary of an employee",
            description = "Returns the calculated salary. Full-time: monthly salary. Part-time: hours × rate."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Salary returned",
                    content = @Content(schema = @Schema(implementation = SalaryResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @GetMapping("/{id}/salary")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_HR') or (hasAuthority('ROLE_EMPLOYEE') and @securityService.isSelf(authentication, #a0))")
    public ResponseEntity<SalaryResponseDTO> getEmployeeSalary(
            @Parameter(description = "Unique ID of the employee", required = true, example = "1")
            @PathVariable Long id) {
        log.info("GET /api/employees/{}/salary", id);
        return ResponseEntity.ok(service.getEmployeeSalary(id));
    }

    // ─── ONBOARD ────────────────────────────────────────────────────────────

    @Operation(
            summary     = "Onboard a new employee",
            description = "Creates a new employee and triggers the full onboarding pipeline: " +
                    "work email, Slack invite, training modules, payroll setup, AI welcome message. " +
                    "Rate limited to 10 requests per minute."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Employee onboarded successfully",
                    content = @Content(schema = @Schema(implementation = OnboardingResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
            @ApiResponse(responseCode = "429", description = "Too many requests — rate limit exceeded",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "500", description = "Onboarding pipeline failed", content = @Content)
    })
    @PostMapping("/onboard")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_HR')")
    public ResponseEntity<?> onboardEmployee(
            @Valid @RequestBody EmployeeRequestDTO dto,
            HttpServletRequest request) {
        // Distributed rate limiting check using Redis
        String clientIp = getClientIp(request);
        String key = "rate_limit:onboard:" + clientIp;
        Long requests = redisTemplate.opsForValue().increment(key);
        if (requests != null && requests == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(1));
        }

        if (requests != null && requests > 10) {
            log.warn("Rate limit exceeded on /onboard endpoint for IP: {}", clientIp);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Too many onboarding requests. Please wait a moment and try again.");
        }
        log.info("POST /api/employees/onboard — name: {}", dto.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addEmployeeWithOnboarding(dto));
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }

    // ─── UPDATE ─────────────────────────────────────────────────────────────

    @Operation(
            summary     = "Update employee details",
            description = "Updates an existing employee's information by ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee updated",
                    content = @Content(schema = @Schema(implementation = EmployeeResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_HR')")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(
            @Parameter(description = "Unique ID of the employee", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequestDTO dto) {
        log.info("PUT /api/employees/{}", id);
        return ResponseEntity.ok(service.updateEmployee(id, dto));
    }

    // ─── DELETE ─────────────────────────────────────────────────────────────

    @Operation(
            summary     = "Soft-delete an employee",
            description = "Marks the employee as deleted (sets deleted_at timestamp). " +
                    "The record is NOT physically removed from the database."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee soft-deleted",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> removeEmployee(
            @Parameter(description = "Unique ID of the employee", required = true, example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/employees/{}", id);
        service.removeEmployee(id);
        return ResponseEntity.ok("Employee ID " + id + " removed successfully.");
    }

    // ─── EXPORT TO CSV ──────────────────────────────────────────────────────

    @Operation(summary = "Export all employees to CSV", description = "Exports list of active employees including basic info, role, and current salary in CSV format. Accessible to ADMIN and HR roles.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CSV file generated successfully",
                    content = @Content(mediaType = "text/csv", schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden — Insufficient permissions", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error generating CSV", content = @Content)
    })
    @GetMapping(value = "/export", produces = "text/csv")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_HR')")
    public ResponseEntity<String> exportToCsv() {
        log.info("GET /api/employees/export");
        String csvData = service.exportToCsv();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"employees.csv\"")
                .body(csvData);
    }
}