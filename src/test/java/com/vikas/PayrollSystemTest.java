package com.vikas;

import com.vikas.dto.EmployeeRequestDTO;
import com.vikas.dto.EmployeeResponseDTO;
import com.vikas.dto.SalaryResponseDTO;
import com.vikas.enums.EmployeeType;
import com.vikas.exception.EmployeeNotFoundException;
import com.vikas.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PayrollSystemTest {

    @Autowired
    private EmployeeService service;

    private EmployeeRequestDTO fullTimeDTO;
    private EmployeeRequestDTO partTimeDTO;

    @BeforeEach
    void setUp() {
        fullTimeDTO = new EmployeeRequestDTO();
        fullTimeDTO.setName("Vikas");
        fullTimeDTO.setDesignation("Engineer");
        fullTimeDTO.setType(EmployeeType.FULLTIME);
        fullTimeDTO.setMonthlySalary(85000);

        partTimeDTO = new EmployeeRequestDTO();
        partTimeDTO.setName("Rahul");
        partTimeDTO.setDesignation("Intern");
        partTimeDTO.setType(EmployeeType.PARTTIME);
        partTimeDTO.setHoursWorked(40);
        partTimeDTO.setHourlyRate(200);
    }

    // --- Add Employee Tests ---
    // These tests use addEmployee() which saves directly to DB without
    // triggering the full AI onboarding pipeline — correct for unit tests
    // that are testing salary, update, delete — not onboarding behaviour.

    @Test
    void addFullTimeEmployee_Success() {
        EmployeeResponseDTO response = service.addEmployee(fullTimeDTO);
        assertNotNull(response.getId());
        assertEquals("Vikas", response.getName());
        assertEquals(85000, response.getSalary());
        assertEquals("FullTimeEmployee", response.getType());
    }

    @Test
    void addPartTimeEmployee_SalaryCalculatedCorrectly() {
        EmployeeResponseDTO response = service.addEmployee(partTimeDTO);
        // 40 hours × 200 rate = 8000
        assertEquals(8000, response.getSalary());
    }

    @Test
    void addEmployee_WithZeroSalary_Success() {
        fullTimeDTO.setMonthlySalary(0);
        EmployeeResponseDTO response = service.addEmployee(fullTimeDTO);
        assertEquals(0, response.getSalary());
    }

    @Test
    void addEmployee_WithZeroHours_SalaryIsZero() {
        partTimeDTO.setHoursWorked(0);
        EmployeeResponseDTO response = service.addEmployee(partTimeDTO);
        assertEquals(0, response.getSalary());
    }

    // --- Get Employee Tests ---

    @Test
    void getAllEmployees_ReturnsCorrectCount() {
        long initialCount = service.getAllEmployees(PageRequest.of(0, 20)).getTotalElements();
        service.addEmployee(fullTimeDTO);
        service.addEmployee(partTimeDTO);
        // CHANGED: getAllEmployees now requires Pageable — Page wraps results
        Page<EmployeeResponseDTO> employees = service.getAllEmployees(PageRequest.of(0, 20));
        assertEquals(initialCount + 2, employees.getTotalElements());
    }

    @Test
    void getEmployeeById_ReturnsCorrectEmployee() {
        EmployeeResponseDTO created = service.addEmployee(fullTimeDTO);
        EmployeeResponseDTO fetched = service.getEmployeeById(created.getId());
        assertEquals("Vikas", fetched.getName());
    }

    @Test
    void getEmployeeById_NotFound_ThrowsException() {
        // CHANGED: now throws EmployeeNotFoundException instead of NoSuchElementException
        assertThrows(EmployeeNotFoundException.class, () -> service.getEmployeeById(9999L));
    }

    // --- Salary Tests ---

    @Test
    void getEmployeeSalary_ReturnsStructuredResponse() {
        EmployeeResponseDTO created = service.addEmployee(fullTimeDTO);
        SalaryResponseDTO salary = service.getEmployeeSalary(created.getId());
        assertEquals(created.getId(), salary.getEmployeeId());
        assertEquals("Vikas", salary.getEmployeeName());
        assertEquals(85000, salary.getSalary());
    }

    @Test
    void getEmployeeSalary_NotFound_ThrowsException() {
        // CHANGED: now throws EmployeeNotFoundException instead of NoSuchElementException
        assertThrows(EmployeeNotFoundException.class, () -> service.getEmployeeSalary(9999L));
    }

    // --- Update Employee Tests ---

    @Test
    void updateEmployee_UpdatesSalaryCorrectly() {
        EmployeeResponseDTO created = service.addEmployee(fullTimeDTO);
        fullTimeDTO.setMonthlySalary(95000);
        EmployeeResponseDTO updated = service.updateEmployee(created.getId(), fullTimeDTO);
        assertEquals(95000, updated.getSalary());
    }

    @Test
    void updateEmployee_ChangingType_ThrowsException() {
        EmployeeResponseDTO created = service.addEmployee(fullTimeDTO);
        partTimeDTO.setName("Vikas");
        assertThrows(IllegalArgumentException.class,
                () -> service.updateEmployee(created.getId(), partTimeDTO));
    }

    @Test
    void updateEmployee_NotFound_ThrowsException() {
        // CHANGED: now throws EmployeeNotFoundException instead of NoSuchElementException
        assertThrows(EmployeeNotFoundException.class,
                () -> service.updateEmployee(9999L, fullTimeDTO));
    }

    // --- Remove Employee Tests ---

    @Test
    void removeEmployee_Success() {
        EmployeeResponseDTO created = service.addEmployee(fullTimeDTO);
        assertDoesNotThrow(() -> service.removeEmployee(created.getId()));
        // After soft delete, getById should throw EmployeeNotFoundException
        assertThrows(EmployeeNotFoundException.class,
                () -> service.getEmployeeById(created.getId()));
    }

    @Test
    void removeNonExistentEmployee_ThrowsException() {
        // CHANGED: now throws EmployeeNotFoundException instead of NoSuchElementException
        assertThrows(EmployeeNotFoundException.class,
                () -> service.removeEmployee(9999L));
    }
}