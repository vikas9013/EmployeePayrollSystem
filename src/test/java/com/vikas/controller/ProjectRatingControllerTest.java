package com.vikas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vikas.config.SecurityConfig;
import com.vikas.dto.ProjectRatingRequestDTO;
import com.vikas.dto.ProjectRatingResponseDTO;
import com.vikas.security.JwtAuthFilter;
import com.vikas.security.JwtUtil;
import com.vikas.security.SecurityService;
import com.vikas.service.ProjectRatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectRatingController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class})
public class ProjectRatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectRatingService projectRatingService;


    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private org.springframework.data.redis.core.StringRedisTemplate stringRedisTemplate;

    @MockBean(name = "securityService")
    private SecurityService securityService;

    private ProjectRatingRequestDTO requestDTO;
    private ProjectRatingResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new ProjectRatingRequestDTO(1L, "Payroll Migration", 4, "Good job");
        responseDTO = new ProjectRatingResponseDTO(1L, 1L, "Payroll Migration", 4, "Good job", LocalDateTime.now(), "admin");
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void testAddRating_AsManager_ShouldReturnCreated() throws Exception {
        Mockito.when(projectRatingService.addRating(any(ProjectRatingRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/ratings")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.projectName").value("Payroll Migration"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void testAddRating_AsEmployee_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/ratings")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetRatingsForEmployee_AsAdmin_ShouldReturnOk() throws Exception {
        Mockito.when(projectRatingService.getRatingsForEmployee(1L)).thenReturn(Collections.singletonList(responseDTO));

        mockMvc.perform(get("/api/ratings/employee/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void testGetRatingsForEmployee_AsSelfEmployee_ShouldReturnOk() throws Exception {
        Mockito.when(securityService.isSelf(any(org.springframework.security.core.Authentication.class), eq(1L))).thenReturn(true);
        Mockito.when(projectRatingService.getRatingsForEmployee(1L)).thenReturn(Collections.singletonList(responseDTO));

        mockMvc.perform(get("/api/ratings/employee/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void testGetRatingsForEmployee_AsOtherEmployee_ShouldReturnForbidden() throws Exception {
        Mockito.when(securityService.isSelf(any(org.springframework.security.core.Authentication.class), eq(2L))).thenReturn(false);

        mockMvc.perform(get("/api/ratings/employee/2"))
                .andExpect(status().isForbidden());
    }
}
