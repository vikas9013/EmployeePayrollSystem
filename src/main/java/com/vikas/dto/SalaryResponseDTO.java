package com.vikas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryResponseDTO implements Serializable {

    private Long   employeeId;
    private String employeeName;
    private double salary;
}