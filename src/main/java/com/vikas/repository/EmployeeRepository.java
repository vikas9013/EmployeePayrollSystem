package com.vikas.repository;

import com.vikas.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // JpaRepository gives us:
    // save(), findById(), findAll(), deleteById(), existsById() — all for free!
}