package com.vikas.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// CHANGED:
//  Added JWT Bearer security scheme so Swagger UI shows the "Authorize" button.
//  After logging in via POST /api/auth/login, paste the token in Swagger's
//  Authorize dialog to call all protected endpoints directly from the UI.

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI employeePayrollOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Employee Payroll System API")
                        .description("REST API for managing employees, payroll, and onboarding. " +
                                "Use POST /api/auth/login to get a JWT token, then click " +
                                "'Authorize' and paste the token to test protected endpoints.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Vikas")
                                .email("vikas@example.com"))
                        .license(new License()
                                .name("MIT License")))
                // Register the Bearer auth scheme — enables the Authorize button in Swagger UI
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Paste your JWT token here (without 'Bearer ' prefix)")));
    }
}
