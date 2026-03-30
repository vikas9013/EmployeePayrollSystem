package com.vikas.repository;

import com.vikas.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// NEW CLASS
// Location: src/main/java/com/vikas/repository/UserRepository.java
// Purpose : DB access for the User entity used during JWT authentication

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
