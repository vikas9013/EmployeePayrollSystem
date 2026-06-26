package com.vikas.config;

import com.vikas.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

import java.util.List;
import java.util.Arrays;

// NEW CLASS
// Location: src/main/java/com/vikas/config/SecurityConfig.java
// Purpose : Configures Spring Security — JWT stateless sessions, route protection,
//           CORS policy, and BCrypt password encoding.

@Configuration
@EnableWebSecurity
@EnableMethodSecurity          // enables @PreAuthorize on controller methods
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Value("${app.cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF — not needed for stateless JWT APIs
                .csrf(AbstractHttpConfigurer::disable)

                // CORS — allow only your frontend origin in production
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // No sessions — each request is authenticated via JWT
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Public endpoints — no token needed
                        .requestMatchers("/api/auth/login", "/api/auth/refresh").permitAll()
                        .requestMatchers(
                                "/",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers("/actuator/health").permitAll()

                        // Read operations — allow ADMIN, HR, and EMPLOYEE (method-level @PreAuthorize will restrict further)
                        .requestMatchers(HttpMethod.GET, "/api/employees/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_HR", "ROLE_EMPLOYEE")

                        // Write operations
                        .requestMatchers(HttpMethod.POST, "/api/employees/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_HR")
                        .requestMatchers(HttpMethod.PUT, "/api/employees/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_HR")
                        .requestMatchers(HttpMethod.DELETE, "/api/employees/**")
                        .hasAuthority("ROLE_ADMIN")

                        // Ratings endpoints
                        .requestMatchers(HttpMethod.GET, "/api/ratings/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER", "ROLE_HR", "ROLE_EMPLOYEE")
                        .requestMatchers(HttpMethod.POST, "/api/ratings/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")

                        .anyRequest().authenticated()
                )

                .headers(headers -> headers
                        .frameOptions(frame -> frame.deny())
                        .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                        .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
                )

                // Add our JWT filter before Spring's default username/password filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}