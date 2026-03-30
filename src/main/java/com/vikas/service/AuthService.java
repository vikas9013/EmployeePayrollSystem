package com.vikas.service;

import com.vikas.dto.LoginRequestDTO;
import com.vikas.dto.LoginResponseDTO;
import com.vikas.entity.User;
import com.vikas.repository.UserRepository;
import com.vikas.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// NEW CLASS
// Location: src/main/java/com/vikas/service/AuthService.java
// Purpose : Handles login — verifies credentials and returns a signed JWT token.

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil         jwtUtil;

    public LoginResponseDTO login(LoginRequestDTO request) {
        log.info("Login attempt for username: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Login failed — username not found: {}", request.getUsername());
                    return new RuntimeException("Invalid username or password");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed — wrong password for username: {}", request.getUsername());
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        log.info("Login successful for username: {}", user.getUsername());

        return new LoginResponseDTO(token, user.getUsername(), user.getRole());
    }
}