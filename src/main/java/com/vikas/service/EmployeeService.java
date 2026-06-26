package com.vikas.service;

import com.vikas.dto.EmployeeRequestDTO;
import com.vikas.dto.EmployeeResponseDTO;
import com.vikas.dto.OnboardingResponseDTO;
import com.vikas.dto.SalaryResponseDTO;
import com.vikas.entity.Employee;
import com.vikas.entity.FullTimeEmployee;
import com.vikas.entity.PartTimeEmployee;
import com.vikas.enums.EmployeeType;
import com.vikas.exception.EmployeeNotFoundException;
import com.vikas.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opencsv.CSVWriter;
import java.io.StringWriter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository repository;
    private final ApplicationEventPublisher applicationEventPublisher;

    // ─── GET ALL (paginated) ────────────────────────────────────────────────

    public Page<EmployeeResponseDTO> getAllEmployees(Pageable pageable) {
        log.info("Fetching all employees — page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return repository.findAll(pageable).map(this::toDTO);
    }

    // ─── GET BY ID ──────────────────────────────────────────────────────────

    @Cacheable(value = "employees", key = "#id")
    public EmployeeResponseDTO getEmployeeById(Long id) {
        log.info("Fetching employee by id: {}", id);
        Employee employee = findByIdOrThrow(id);
        return toDTO(employee);
    }

    // ─── GET SALARY ─────────────────────────────────────────────────────────

    public SalaryResponseDTO getEmployeeSalary(Long id) {
        log.info("Fetching salary for employee id: {}", id);
        Employee employee = findByIdOrThrow(id);
        return new SalaryResponseDTO(
                employee.getId(),
                employee.getName(),
                employee.calculateSalary()
        );
    }

    // ─── ADD EMPLOYEE (simple save — no onboarding pipeline) ────────────────
    // Purpose : Saves employee directly to DB without triggering the 5-step
    //           onboarding pipeline (email, Slack, training, payroll, AI).
    //           Used in integration tests where we just need a saved employee
    //           to test getById, getSalary, update, delete — not onboarding.

    @Transactional
    @CacheEvict(value = "employees", allEntries = true)
    public EmployeeResponseDTO addEmployee(EmployeeRequestDTO dto) {
        log.info("Saving employee without onboarding: {}", dto.getName());
        Employee employee = buildEmployee(dto);
        repository.save(employee);
        log.info("Employee saved with id: {}", employee.getId());
        return toDTO(employee);
    }

    // ─── ONBOARD (save + full 5-step pipeline) ──────────────────────────────
    // Purpose : Saves employee AND triggers the full onboarding pipeline.
    //           This is what the actual POST /api/employees/onboard calls.

    @Transactional
    @CacheEvict(value = "employees", allEntries = true)
    public OnboardingResponseDTO addEmployeeWithOnboarding(EmployeeRequestDTO dto) {
        log.info("Starting onboarding for new employee: {}", dto.getName());
        Employee employee = buildEmployee(dto);
        repository.save(employee);
        log.info("Employee saved with id: {}", employee.getId());
        
        // Publish event for async processing
        applicationEventPublisher.publishEvent(new com.vikas.event.OnboardingEvent(this, employee));
        
        log.info("Onboarding event published for employee id: {}", employee.getId());
        
        return OnboardingResponseDTO.builder()
                .employeeId(employee.getId())
                .employeeName(employee.getName())
                .message("Onboarding process started asynchronously in the background.")
                .build();
    }

    // ─── UPDATE ─────────────────────────────────────────────────────────────

    @Transactional
    @CacheEvict(value = "employees", allEntries = true)
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO dto) {
        log.info("Updating employee id: {}", id);
        Employee existing = findByIdOrThrow(id);

        existing.setName(dto.getName());
        existing.setDesignation(dto.getDesignation());

        if (existing instanceof FullTimeEmployee fte
                && dto.getType() == EmployeeType.FULLTIME) {
            fte.setMonthlySalary(dto.getMonthlySalary());

        } else if (existing instanceof PartTimeEmployee pte
                && dto.getType() == EmployeeType.PARTTIME) {
            pte.setHoursWorked(dto.getHoursWorked());
            pte.setHourlyRate(dto.getHourlyRate());

        } else {
            throw new IllegalArgumentException(
                    "Cannot change employee type. Employee " + id
                            + " is a " + existing.getClass().getSimpleName() + ".");
        }

        repository.save(existing);
        log.info("Employee id: {} updated successfully", id);
        return toDTO(existing);
    }

    // ─── DELETE (soft) ──────────────────────────────────────────────────────

    @Transactional
    @CacheEvict(value = "employees", allEntries = true)
    public void removeEmployee(Long id) {
        log.info("Soft-deleting employee id: {}", id);
        if (!repository.existsById(id)) {
            throw new EmployeeNotFoundException(id);
        }
        // @SQLDelete on Entity sets deleted_at — row is NOT physically removed
        repository.deleteById(id);
        log.info("Employee id: {} soft-deleted", id);
    }

    // ─── SAVE ONBOARDING PROGRESS ───────────────────────────────────────────

    @Transactional
    @CacheEvict(value = "employees", key = "#employee.id")
    public void saveOnboardingProgress(Employee employee) {
        log.info("Saving onboarding progress for employee id: {}", employee.getId());
        repository.save(employee);
    }

    // ─── PRIVATE HELPERS ────────────────────────────────────────────────────

    // ─── EXPORT TO CSV ──────────────────────────────────────────────────────

    public String exportToCsv() {
        log.info("Exporting all employees to CSV");
        List<Employee> employees = repository.findAll();
        
        try (StringWriter sw = new StringWriter();
             CSVWriter csvWriter = new CSVWriter(sw)) {
             
            String[] header = {"ID", "Name", "Designation", "Salary"};
            csvWriter.writeNext(header);
            
            for (Employee employee : employees) {
                String[] row = {
                        employee.getId().toString(),
                        employee.getName(),
                        employee.getDesignation(),
                        String.valueOf(employee.calculateSalary())
                };
                csvWriter.writeNext(row);
            }
            return sw.toString();
        } catch (Exception e) {
            log.error("Error generating CSV", e);
            throw new RuntimeException("Error generating CSV");
        }
    }

    private Employee findByIdOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    private Employee buildEmployee(EmployeeRequestDTO dto) {
        return switch (dto.getType()) {
            case FULLTIME -> new FullTimeEmployee(
                    dto.getName(), dto.getDesignation(), dto.getMonthlySalary());
            case PARTTIME -> new PartTimeEmployee(
                    dto.getName(), dto.getDesignation(),
                    dto.getHoursWorked(), dto.getHourlyRate());
            default -> throw new IllegalArgumentException(
                    "Invalid type. Use FULLTIME or PARTTIME.");
        };
    }

    private EmployeeResponseDTO toDTO(Employee employee) {
        return new EmployeeResponseDTO(
                employee.getId(),
                employee.getName(),
                employee.getDesignation(),
                employee.getClass().getSimpleName(),
                employee.calculateSalary(),
                employee.getWorkEmail(),
                employee.getSlackInviteSent() != null && employee.getSlackInviteSent(),
                employee.getTrainingAssigned() != null && employee.getTrainingAssigned(),
                employee.getPayrollConfigured() != null && employee.getPayrollConfigured(),
                employee.getAiOnboardingMessage()
        );
    }
}