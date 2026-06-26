package com.vikas.service;

import com.vikas.dto.ProjectRatingRequestDTO;
import com.vikas.dto.ProjectRatingResponseDTO;
import com.vikas.entity.Employee;
import com.vikas.entity.ProjectRating;
import com.vikas.exception.EmployeeNotFoundException;
import com.vikas.repository.EmployeeRepository;
import com.vikas.repository.ProjectRatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectRatingService {

    private final ProjectRatingRepository projectRatingRepository;
    private final EmployeeRepository employeeRepository;

    @CacheEvict(value = "ratings", allEntries = true)
    public ProjectRatingResponseDTO addRating(ProjectRatingRequestDTO requestDTO) {
        Employee employee = employeeRepository.findById(requestDTO.getEmployeeId())
                .orElseThrow(() -> new EmployeeNotFoundException(requestDTO.getEmployeeId()));

        ProjectRating rating = new ProjectRating();
        rating.setEmployee(employee);
        rating.setProjectName(requestDTO.getProjectName());
        rating.setScore(requestDTO.getScore());
        rating.setFeedback(requestDTO.getFeedback());

        ProjectRating savedRating = projectRatingRepository.save(rating);
        return mapToResponseDTO(savedRating);
    }

    @Cacheable(value = "ratings", key = "'employee_' + #employeeId")
    public List<ProjectRatingResponseDTO> getRatingsForEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new EmployeeNotFoundException(employeeId);
        }

        return projectRatingRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "ratings", key = "'all'")
    public List<ProjectRatingResponseDTO> getAllRatings() {
        return projectRatingRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private ProjectRatingResponseDTO mapToResponseDTO(ProjectRating rating) {
        return new ProjectRatingResponseDTO(
                rating.getId(),
                rating.getEmployee().getId(),
                rating.getProjectName(),
                rating.getScore(),
                rating.getFeedback(),
                rating.getCreatedAt(),
                rating.getCreatedBy()
        );
    }
}
