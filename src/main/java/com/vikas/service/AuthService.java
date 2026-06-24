package com.vikas.service;

import com.vikas.dto.LoginRequestDTO;
import com.vikas.dto.LoginResponseDTO;
import com.vikas.dto.RefreshTokenRequestDTO;
import com.vikas.entity.RefreshToken;
import com.vikas.entity.User;
import com.vikas.repository.RefreshTokenRepository;
import com.vikas.repository.UserRepository;
import com.vikas.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository         userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder        passwordEncoder;
    private final JwtUtil                jwtUtil;

    @Transactional
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
        String refreshTokenStr = createRefreshToken(user);

        log.info("Login successful for username: {}", user.getUsername());
        return new LoginResponseDTO(token, refreshTokenStr, user.getUsername(), user.getRole());
    }

    @Transactional
    public LoginResponseDTO refresh(RefreshTokenRequestDTO request) {
        log.info("Refresh token attempt");

        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }

        User user = refreshToken.getUser();
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        
        log.info("Token refreshed for username: {}", user.getUsername());
        return new LoginResponseDTO(token, refreshToken.getToken(), user.getUsername(), user.getRole());
    }

    private String createRefreshToken(User user) {
        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.flush();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(86400000L * 7)); // 7 days
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }
}