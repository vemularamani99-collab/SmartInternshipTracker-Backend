package com.smartinternship.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartinternship.backend.config.SecurityConfig;
import com.smartinternship.backend.dto.InternshipRequest;
import com.smartinternship.backend.dto.InternshipResponse;
import com.smartinternship.backend.dto.PaginatedResponse;
import com.smartinternship.backend.exception.GlobalExceptionHandler;
import com.smartinternship.backend.security.CustomUserDetailsService;
import com.smartinternship.backend.security.JwtAccessDeniedHandler;
import com.smartinternship.backend.security.JwtAuthenticationEntryPoint;
import com.smartinternship.backend.security.JwtAuthenticationFilter;
import com.smartinternship.backend.security.JwtUtils;
import com.smartinternship.backend.service.InternshipService;
import com.smartinternship.backend.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security Integration tests verifying role-based access control (RBAC):
 * - Public browsing permitted without token
 * - Admin protected endpoints block unauthenticated requests (401)
 * - Admin protected endpoints block student requests (403)
 * - Admin permitted with proper authority (ROLE_ADMIN)
 */
@WebMvcTest(controllers = {InternshipController.class, AdminUserController.class})
@Import({SecurityConfig.class, GlobalExceptionHandler.class, JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class})
class InternshipSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InternshipService internshipService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @org.junit.jupiter.api.BeforeEach
    void setupFilterPassThrough() throws Exception {
        org.mockito.Mockito.doAnswer(invocation -> {
            jakarta.servlet.ServletRequest req = invocation.getArgument(0);
            jakarta.servlet.ServletResponse res = invocation.getArgument(1);
            jakarta.servlet.FilterChain chain = invocation.getArgument(2);
            chain.doFilter(req, res);
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
    }

    @Test
    @DisplayName("GET /api/internships - Public browsing is permitted without authentication (HTTP 200)")
    void testPublicCanBrowseInternships() throws Exception {
        PaginatedResponse<InternshipResponse> emptyPage = new PaginatedResponse<>(
            Collections.emptyList(), 0, 10, 0, 0, true, true
        );
        when(internshipService.getAllInternships(anyInt(), anyInt(), anyString(), anyString(), any()))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/api/internships"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/internships - Unauthenticated request fails with HTTP 401 Unauthorized")
    void testUnauthenticatedCannotCreateInternship() throws Exception {
        InternshipRequest req = new InternshipRequest();
        req.setTitle("DevOps Intern");
        req.setCompany("Cloud Corp");
        req.setLocation("Remote");
        req.setDescription("CI/CD pipelines");
        req.setDeadline(LocalDate.now().plusDays(30));

        mockMvc.perform(post("/api/internships")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "student@example.com", roles = {"STUDENT"})
    @DisplayName("POST /api/internships - Student request is denied with HTTP 403 Forbidden")
    void testStudentCannotCreateInternship() throws Exception {
        InternshipRequest req = new InternshipRequest();
        req.setTitle("DevOps Intern");
        req.setCompany("Cloud Corp");
        req.setLocation("Remote");
        req.setDescription("CI/CD pipelines");
        req.setDeadline(LocalDate.now().plusDays(30));

        mockMvc.perform(post("/api/internships")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    @DisplayName("POST /api/internships - Admin request succeeds with HTTP 201 Created")
    void testAdminCanCreateInternship() throws Exception {
        InternshipRequest req = new InternshipRequest();
        req.setTitle("DevOps Intern");
        req.setCompany("Cloud Corp");
        req.setLocation("Remote");
        req.setDescription("CI/CD pipelines");
        req.setDeadline(LocalDate.now().plusDays(30));
        req.setStipend(40000.0);

        InternshipResponse res = new InternshipResponse();
        res.setId(1L);
        res.setTitle("DevOps Intern");
        res.setCompany("Cloud Corp");

        when(internshipService.createInternship(any(InternshipRequest.class))).thenReturn(res);

        mockMvc.perform(post("/api/internships")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "student@example.com", roles = {"STUDENT"})
    @DisplayName("GET /api/admin/users - Student cannot view registered users directory (HTTP 403 Forbidden)")
    void testStudentCannotViewAdminUsers() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    @DisplayName("GET /api/admin/users - Admin can view registered users directory (HTTP 200 OK)")
    void testAdminCanViewUsers() throws Exception {
        when(userService.getAllUsers(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk());
    }
}
