package com.vikas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vikas.dto.EmployeeRequestDTO;
import com.vikas.dto.EmployeeResponseDTO;
import com.vikas.dto.OnboardingResponseDTO;
import com.vikas.dto.SalaryResponseDTO;
import com.vikas.enums.EmployeeType;
import com.vikas.exception.EmployeeNotFoundException;
import com.vikas.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllEmployees_Returns200WithList() throws Exception {
        // CHANGED: getAllEmployees now takes Pageable and returns Page<>
        when(service.getAllEmployees(any(Pageable.class))).thenReturn(
                new PageImpl<>(List.of(
                        new EmployeeResponseDTO(1L, "Vikas", "Engineer", "FullTimeEmployee", 85000)
                ))
        );

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Vikas"))
                .andExpect(jsonPath("$.content[0].salary").value(85000));
    }

    @Test
    void getEmployeeById_Returns200() throws Exception {
        when(service.getEmployeeById(1L))
                .thenReturn(new EmployeeResponseDTO(1L, "Vikas", "Engineer", "FullTimeEmployee", 85000));
        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Vikas"));
    }

    @Test
    void getEmployeeById_NotFound_Returns404() throws Exception {
        // CHANGED: now throws EmployeeNotFoundException instead of NoSuchElementException
        when(service.getEmployeeById(999L)).thenThrow(new EmployeeNotFoundException(999L));

        mockMvc.perform(get("/api/employees/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getEmployeeSalary_Returns200WithStructuredBody() throws Exception {
        when(service.getEmployeeSalary(1L))
                .thenReturn(new SalaryResponseDTO(1L, "Vikas", 85000));

        mockMvc.perform(get("/api/employees/1/salary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(1))
                .andExpect(jsonPath("$.salary").value(85000));
    }

    @Test
    void addEmployee_ValidRequest_Returns201() throws Exception {
        EmployeeRequestDTO dto = new EmployeeRequestDTO();
        dto.setName("Vikas");
        dto.setDesignation("Engineer");
        dto.setType(EmployeeType.FULLTIME);
        dto.setMonthlySalary(85000);

        when(service.addEmployeeWithOnboarding(any())).thenReturn(
                new OnboardingResponseDTO(1L, "Vikas", "vikas@company.com",
                        true, true, true,
                        "Onboarding completed", "Welcome Vikas!"));

        mockMvc.perform(post("/api/employees/onboard")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employeeId").value(1));
    }

    @Test
    void addEmployee_MissingName_Returns400() throws Exception {
        EmployeeRequestDTO dto = new EmployeeRequestDTO();
        dto.setType(EmployeeType.FULLTIME); // name is missing

        mockMvc.perform(post("/api/employees/onboard")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateEmployee_NotFound_Returns404() throws Exception {
        EmployeeRequestDTO dto = new EmployeeRequestDTO();
        dto.setName("Vikas");
        dto.setDesignation("Engineer");
        dto.setType(EmployeeType.FULLTIME);
        dto.setMonthlySalary(90000);

        // CHANGED: now throws EmployeeNotFoundException instead of NoSuchElementException
        when(service.updateEmployee(eq(999L), any()))
                .thenThrow(new EmployeeNotFoundException(999L));

        mockMvc.perform(put("/api/employees/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteEmployee_Returns200() throws Exception {
        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Employee ID 1 removed successfully."));
    }

    @Test
    void deleteEmployee_NotFound_Returns404() throws Exception {
        // CHANGED: now throws EmployeeNotFoundException instead of NoSuchElementException
        doThrow(new EmployeeNotFoundException(999L))
                .when(service).removeEmployee(999L);

        mockMvc.perform(delete("/api/employees/999"))
                .andExpect(status().isNotFound());
    }
}