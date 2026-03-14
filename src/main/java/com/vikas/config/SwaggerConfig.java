package com.vikas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI employeePayrollOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Employee Payroll System API")
                        .description("REST API for managing employees, payroll, and onboarding")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Vikas")
                                .email("vikas@example.com"))
                        .license(new License()
                                .name("MIT License")));
    }
}
