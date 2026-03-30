package com.vikas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// NEW CLASS
// Location: src/main/java/com/vikas/dto/LoginRequestDTO.java
// Purpose : Request body for POST /api/auth/login

@Data
public class LoginRequestDTO {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}