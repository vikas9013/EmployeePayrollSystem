package com.vikas.service;

import com.vikas.dto.EmployeeRequestDTO;
import com.vikas.dto.EmployeeResponseDTO;
import com.vikas.dto.SalaryResponseDTO;
import com.vikas.entity.Employee;
import com.vikas.entity.FullTimeEmployee;
import com.vikas.entity.PartTimeEmployee;
import com.vikas.enums.EmployeeType;
import com.vikas.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import com.vikas.dto.OnboardingResponseDTO;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;
    private final OnboardingService  onboardingService;

    public EmployeeService(EmployeeRepository repository,
                           OnboardingService onboardingService) {
        this.repository        = repository;
        this.onboardingService = onboardingService;
    }


    // Add a new employee
    public EmployeeResponseDTO addEmployee(EmployeeRequestDTO dto) {
        Employee employee = buildEmployee(dto);
        repository.save(employee);
        return toDTO(employee);
    }

    // Save + run full onboarding pipeline
    public OnboardingResponseDTO addEmployeeWithOnboarding(EmployeeRequestDTO dto) {
        Employee employee = buildEmployee(dto);
        repository.save(employee);                   // save FIRST to get the ID
        return onboardingService.onboard(employee);
    }

    // Shared helper — builds entity from DTO
    private Employee buildEmployee(EmployeeRequestDTO dto) {
        if (dto.getType() == EmployeeType.FULLTIME) {
            return new FullTimeEmployee(dto.getName(), dto.getDesignation(),
                    dto.getMonthlySalary());
        } else if (dto.getType() == EmployeeType.PARTTIME) {
            return new PartTimeEmployee(dto.getName(), dto.getDesignation(),
                    dto.getHoursWorked(), dto.getHourlyRate());
        } else {
            throw new IllegalArgumentException("Invalid type. Use FULLTIME or PARTTIME.");
        }
    }

    // Get all employees
    public List<EmployeeResponseDTO> getAllEmployees() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // Get employee by ID
    public EmployeeResponseDTO getEmployeeById(Long id) {
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Employee ID " + id + " not found."));
        return toDTO(employee);
    }

    // Get salary of an employee — returns structured DTO
    public SalaryResponseDTO getEmployeeSalary(Long id) {
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Employee ID " + id + " not found."));
        return new SalaryResponseDTO(employee.getId(), employee.getName(), employee.calculateSalary());
    }

    // Update an employee
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO dto) {
        Employee existing = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Employee ID " + id + " not found."));

        existing.setName(dto.getName());

        if (existing instanceof FullTimeEmployee fte && dto.getType() == EmployeeType.FULLTIME) {
            fte.setMonthlySalary(dto.getMonthlySalary());
        } else if (existing instanceof PartTimeEmployee pte && dto.getType() == EmployeeType.PARTTIME) {
            pte.setHoursWorked(dto.getHoursWorked());
            pte.setHourlyRate(dto.getHourlyRate());
        } else {
            throw new IllegalArgumentException(
                    "Cannot change employee type. Employee " + id + " is a " +
                            existing.getClass().getSimpleName() + ".");
        }

        repository.save(existing);
        return toDTO(existing);
    }

    // Remove an employee
    public void removeEmployee(Long id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Employee ID " + id + " not found.");
        }
        repository.deleteById(id);
    }

    // Convert Entity → ResponseDTO
    private EmployeeResponseDTO toDTO(Employee employee) {
        return new EmployeeResponseDTO(
                employee.getId(),
                employee.getName(),
                employee.getDesignation(),
                employee.getClass().getSimpleName(),
                employee.calculateSalary()
        );
    }
}