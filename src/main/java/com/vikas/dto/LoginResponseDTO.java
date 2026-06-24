package com.vikas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// NEW CLASS
// Location: src/main/java/com/vikas/dto/LoginResponseDTO.java
// Purpose : Response body for POST /api/auth/login — returns the JWT token

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@Schema(description = "Response body containing JWT token, refresh token, and user meta details")
public class LoginResponseDTO {

    @Schema(description = "Access token (JWT) to be passed in authorization header as Bearer token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "Long-lived refresh token to acquire a new access token", example = "a25dfd6f-ea90-410a-9d93-3d0b30cb99bb")
    private String refreshToken;

    @Schema(description = "Username of the logged in user", example = "admin")
    private String username;

    @Schema(description = "Role assigned to the user", example = "ROLE_ADMIN")
    private String role;
}