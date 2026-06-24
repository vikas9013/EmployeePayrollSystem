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

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "Authentication", description = "Endpoints for logging in and obtaining JWT access/refresh tokens")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary     = "Login and retrieve authentication tokens",
            description = "Authenticate with username and password. On success, returns an access token (JWT) " +
                    "and a refresh token. Pass the access token in the header as: 'Authorization: Bearer <token>'."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully logged in",
                    content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials format or missing fields",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Invalid username or password",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(
            summary     = "Refresh the JWT access token",
            description = "Provide a valid refresh token. Returns a new JWT access token and returns the same refresh token."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Access token refreshed successfully",
                    content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid refresh token supplied",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Expired refresh token, login is required",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(@Valid @RequestBody com.vikas.dto.RefreshTokenRequestDTO request) {
        return ResponseEntity.ok(authService.refresh(request));
    }
}