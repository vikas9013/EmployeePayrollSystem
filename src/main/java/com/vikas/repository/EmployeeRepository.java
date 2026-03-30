package com.vikas.repository;

import com.vikas.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// CHANGED:
//  1. Added findAll(Pageable) — replaces the "load all into memory" pattern
//  2. Added findByDesignationContainingIgnoreCase — for basic search/filter

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Paginated list — use this in getAllEmployees() instead of findAll()
    Page<Employee> findAll(Pageable pageable);

    // Search by designation keyword (case-insensitive)
    Page<Employee> findByDesignationContainingIgnoreCase(String designation, Pageable pageable);
}