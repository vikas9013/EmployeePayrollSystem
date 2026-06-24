package com.vikas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// NEW CLASS
// Location: src/main/java/com/vikas/dto/LoginRequestDTO.java
// Purpose : Request body for POST /api/auth/login

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Request body containing credentials for user login")
public class LoginRequestDTO {

    @NotBlank(message = "Username is required")
    @Schema(description = "Registered username", example = "admin")
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(description = "Raw text password", example = "admin123")
    private String password;
}