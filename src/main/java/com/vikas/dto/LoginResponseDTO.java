package com.vikas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// NEW CLASS
// Location: src/main/java/com/vikas/dto/LoginResponseDTO.java
// Purpose : Response body for POST /api/auth/login — returns the JWT token

@Data
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;
    private String username;
    private String role;
}