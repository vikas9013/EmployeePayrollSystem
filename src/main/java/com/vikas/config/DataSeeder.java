package com.vikas.config;

import com.vikas.entity.User;
import com.vikas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

// NEW CLASS
// Location: src/main/java/com/vikas/config/DataSeeder.java
// Purpose : Creates a default admin user on first startup so you can immediately
//           call POST /api/auth/login and get a token.
//
//           Default credentials:
//             username: admin
//             password: admin123
//
//           IMPORTANT: Change the password immediately in production!

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository  userRepository;
    private final com.vikas.repository.EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedDefaultAdmin() {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .role("ROLE_ADMIN")
                        .build();
                userRepository.save(admin);
                log.info("Default admin user created. Username: admin | Password: admin123");
                log.warn("IMPORTANT: Change the default admin password before going to production!");
            }

            if (!userRepository.existsByUsername("hr")) {
                User hrUser = User.builder()
                        .username("hr")
                        .password(passwordEncoder.encode("hr123"))
                        .role("ROLE_HR")
                        .build();
                userRepository.save(hrUser);
                log.info("Default HR user created. Username: hr | Password: hr123");
            }

            if (!userRepository.existsByUsername("employee")) {
                Long employeeId = 1L;
                if (employeeRepository.count() == 0) {
                    com.vikas.entity.FullTimeEmployee seedEmp = new com.vikas.entity.FullTimeEmployee();
                    seedEmp.setName("Vikas Employee");
                    seedEmp.setDesignation("Software Engineer");
                    seedEmp.setMonthlySalary(60000.0);
                    seedEmp.setWorkEmail("employee@company.com");
                    seedEmp.setSlackInviteSent(true);
                    seedEmp.setTrainingAssigned(true);
                    seedEmp.setPayrollConfigured(true);
                    seedEmp.setAiOnboardingMessage("Welcome to the team, Vikas!");
                    seedEmp = employeeRepository.save(seedEmp);
                    employeeId = seedEmp.getId();
                    log.info("Seeded default employee: {} with ID {}", seedEmp.getName(), employeeId);
                } else {
                    employeeId = employeeRepository.findAll().get(0).getId();
                }

                User empUser = User.builder()
                        .username("employee")
                        .password(passwordEncoder.encode("employee123"))
                        .role("ROLE_EMPLOYEE")
                        .employeeId(employeeId)
                        .build();
                userRepository.save(empUser);
                log.info("Default Employee user created. Username: employee | Password: employee123 | Linked Employee ID: {}", employeeId);
            }
        };
    }
}