package com.vikas.controller;

import com.vikas.dto.ProjectRatingRequestDTO;
import com.vikas.dto.ProjectRatingResponseDTO;
import com.vikas.service.ProjectRatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@Tag(name = "Project Rating API", description = "Endpoints for managing employee project ratings")
public class ProjectRatingController {

    private final ProjectRatingService projectRatingService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @Operation(summary = "Add a new project rating", description = "Allows admins to rate an employee's performance on a project.")
    @ApiResponse(responseCode = "201", description = "Rating successfully added")
    @ApiResponse(responseCode = "400", description = "Invalid request payload")
    @ApiResponse(responseCode = "404", description = "Employee not found")
    public ResponseEntity<ProjectRatingResponseDTO> addRating(@Valid @RequestBody ProjectRatingRequestDTO requestDTO) {
        ProjectRatingResponseDTO response = projectRatingService.addRating(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/employee/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER') or (hasAuthority('ROLE_EMPLOYEE') and @securityService.isSelf(authentication, #a0))")
    @Operation(summary = "Get ratings for an employee", description = "Allows an employee to view their own ratings, or admins to view any employee's ratings.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved ratings")
    @ApiResponse(responseCode = "404", description = "Employee not found")
    public ResponseEntity<List<ProjectRatingResponseDTO>> getRatingsForEmployee(@PathVariable Long id) {
        List<ProjectRatingResponseDTO> ratings = projectRatingService.getRatingsForEmployee(id);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_HR')")
    @Operation(summary = "Get all ratings", description = "Allows admins to view all project ratings across the company.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all ratings")
    public ResponseEntity<List<ProjectRatingResponseDTO>> getAllRatings() {
        List<ProjectRatingResponseDTO> ratings = projectRatingService.getAllRatings();
        return ResponseEntity.ok(ratings);
    }
}
