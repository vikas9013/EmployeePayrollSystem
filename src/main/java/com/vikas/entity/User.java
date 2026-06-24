package com.vikas.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// NEW CLASS
// Location: src/main/java/com/vikas/entity/User.java
// Purpose : Stores login credentials. Flyway V1 creates the "users" table.

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;   // BCrypt hashed

    @Column(nullable = false)
    private String role;       // e.g. ROLE_ADMIN, ROLE_HR, ROLE_EMPLOYEE

    @Column(name = "employee_id", unique = true)
    private Long employeeId;
}