package com.vikas.controller;

import com.vikas.dto.LoginRequestDTO;
import com.vikas.dto.LoginResponseDTO;
import com.vikas.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// NEW CLASS
// Location: src/main/java/com/vikas/controller/AuthController.java
// Purpose : Exposes POST /api/auth/login — returns a JWT token on successful login.
//           No authentication required to call this endpoint.

@Tag(name = "Authentication", description = "Login and obtain a JWT token")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary     = "Login",
            description = "Provide username + password. Returns a JWT token to use as: " +
                    "Authorization: Bearer <token> on all protected endpoints."
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(
            summary     = "Refresh Token",
            description = "Provide a valid refresh token to get a new JWT access token."
    )
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(@Valid @RequestBody com.vikas.dto.RefreshTokenRequestDTO request) {
        return ResponseEntity.ok(authService.refresh(request));
    }
}