package com.vikas;

import com.vikas.dto.EmployeeRequestDTO;
import com.vikas.dto.EmployeeResponseDTO;
import com.vikas.dto.SalaryResponseDTO;
import com.vikas.enums.EmployeeType;
import com.vikas.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional  // Each test runs in its own transaction, rolled back automatically — no manual cleanup needed
class PayrollSystemTest {

    @Autowired
    private EmployeeService service;

    private EmployeeRequestDTO fullTimeDTO;
    private EmployeeRequestDTO partTimeDTO;

    @BeforeEach
    void setUp() {
        fullTimeDTO = new EmployeeRequestDTO();
        fullTimeDTO.setName("Vikas");
        fullTimeDTO.setType(EmployeeType.FULLTIME);
        fullTimeDTO.setMonthlySalary(85000);

        partTimeDTO = new EmployeeRequestDTO();
        partTimeDTO.setName("Rahul");
        partTimeDTO.setType(EmployeeType.PARTTIME);
        partTimeDTO.setHoursWorked(40);
        partTimeDTO.setHourlyRate(200);
    }

    // --- Add Employee Tests ---

    @Test
    void addFullTimeEmployee_Success() {
        EmployeeResponseDTO response = service.addEmployee(fullTimeDTO);
        assertNotNull(response.getId()); // ID is auto-generated
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
        service.addEmployee(fullTimeDTO);
        service.addEmployee(partTimeDTO);
        List<EmployeeResponseDTO> employees = service.getAllEmployees();
        assertEquals(2, employees.size());
    }

    @Test
    void getEmployeeById_ReturnsCorrectEmployee() {
        EmployeeResponseDTO created = service.addEmployee(fullTimeDTO);
        EmployeeResponseDTO fetched = service.getEmployeeById(created.getId());
        assertEquals("Vikas", fetched.getName());
    }

    @Test
    void getEmployeeById_NotFound_ThrowsException() {
        assertThrows(NoSuchElementException.class, () -> service.getEmployeeById(9999L));
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
        assertThrows(NoSuchElementException.class, () -> service.getEmployeeSalary(9999L));
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
        // Attempt to update a FULLTIME employee with PARTTIME data
        partTimeDTO.setName("Vikas");
        assertThrows(IllegalArgumentException.class,
                () -> service.updateEmployee(created.getId(), partTimeDTO));
    }

    @Test
    void updateEmployee_NotFound_ThrowsException() {
        assertThrows(NoSuchElementException.class,
                () -> service.updateEmployee(9999L, fullTimeDTO));
    }

    // --- Remove Employee Tests ---

    @Test
    void removeEmployee_Success() {
        EmployeeResponseDTO created = service.addEmployee(fullTimeDTO);
        assertDoesNotThrow(() -> service.removeEmployee(created.getId()));
        assertThrows(NoSuchElementException.class, () -> service.getEmployeeById(created.getId()));
    }

    @Test
    void removeNonExistentEmployee_ThrowsException() {
        assertThrows(NoSuchElementException.class, () -> service.removeEmployee(9999L));
    }
}