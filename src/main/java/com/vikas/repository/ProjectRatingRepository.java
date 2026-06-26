package com.vikas.repository;

import com.vikas.entity.ProjectRating;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRatingRepository extends JpaRepository<ProjectRating, Long> {
    @EntityGraph(attributePaths = {"employee"})
    List<ProjectRating> findByEmployeeId(Long employeeId);

    @EntityGraph(attributePaths = {"employee"})
    List<ProjectRating> findAll();
}
