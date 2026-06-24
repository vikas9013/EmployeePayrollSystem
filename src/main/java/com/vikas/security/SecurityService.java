package com.vikas.security;

import com.vikas.repository.UserRepository;
import com.vikas.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final UserRepository userRepository;

    /**
     * Checks if the authenticated user has an employee_id that matches the requested employeeId.
     */
    public boolean isSelf(Authentication authentication, Long employeeId) {
        if (authentication == null || !authentication.isAuthenticated() || employeeId == null) {
            log.warn("[SecurityService] Denied: Authentication is null or unauthenticated");
            return false;
        }

        String username = authentication.getName();
        log.info("[SecurityService] Checking access for user '{}' on employeeId {}", username, employeeId);

        return userRepository.findByUsername(username)
                .map(User::getEmployeeId)
                .map(id -> {
                    boolean matches = id.equals(employeeId);
                    if (!matches) {
                        log.warn("[SecurityService] Denied: User '{}' (linked to employee {}) is trying to access employee {}", 
                                username, id, employeeId);
                    } else {
                        log.info("[SecurityService] Approved: User '{}' matches employeeId {}", username, employeeId);
                    }
                    return matches;
                })
                .orElseGet(() -> {
                    log.warn("[SecurityService] Denied: User '{}' does not exist or has no linked employee_id", username);
                    return false;
                });
    }
}
