package com.smartinternship.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartinternship.backend.dto.AuthResponse;
import com.smartinternship.backend.dto.LoginRequest;
import com.smartinternship.backend.dto.RegisterRequest;
import com.smartinternship.backend.dto.UserResponse;
import com.smartinternship.backend.exception.BadRequestException;
import com.smartinternship.backend.exception.GlobalExceptionHandler;
import com.smartinternship.backend.model.Role;
import com.smartinternship.backend.security.CustomUserDetailsService;
import com.smartinternship.backend.security.JwtAccessDeniedHandler;
import com.smartinternship.backend.security.JwtAuthenticationEntryPoint;
import com.smartinternship.backend.security.JwtAuthenticationFilter;
import com.smartinternship.backend.security.JwtUtils;
import com.smartinternship.backend.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration / Controller tests for AuthController validating endpoints, JSON serialization,
 * Bean validation rules, and centralized error responses.
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    // Security mocks required for ApplicationContext initialization
    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Test
    @DisplayName("POST /api/auth/register - Valid payload returns HTTP 201 Created")
    void testRegisterValidPayloadReturns201() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("Rohan Patel");
        request.setEmail("rohan@example.com");
        request.setPassword("password123");
        request.setCollege("BITS Pilani");
        request.setDegree("B.E. Computer Science");
        request.setGraduationYear(2026);

        UserResponse userResponse = new UserResponse(
            1L, "Rohan Patel", "rohan@example.com", Role.STUDENT,
            "BITS Pilani", "B.E. Computer Science", 2026, LocalDateTime.now()
        );
        AuthResponse authResponse = new AuthResponse("mocked-jwt-token", 86400000L, userResponse);

        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.data.user.email").value("rohan@example.com"))
                .andExpect(jsonPath("$.data.user.role").value("STUDENT"));
    }

    @Test
    @DisplayName("POST /api/auth/register - Fails Bean validation on blank name and short password")
    void testRegisterBeanValidationFailure() throws Exception {
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setName(""); // blank
        invalidRequest.setEmail("invalid-email-format"); // not an email
        invalidRequest.setPassword("123"); // less than 6 chars

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.password").exists());
    }

    @Test
    @DisplayName("POST /api/auth/login - Valid credentials returns HTTP 200 OK")
    void testLoginValidCredentialsReturns200() throws Exception {
        LoginRequest request = new LoginRequest("rohan@example.com", "password123");

        UserResponse userResponse = new UserResponse(
            1L, "Rohan Patel", "rohan@example.com", Role.STUDENT,
            "BITS Pilani", "B.E. Computer Science", 2026, LocalDateTime.now()
        );
        AuthResponse authResponse = new AuthResponse("mocked-login-token", 86400000L, userResponse);

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("mocked-login-token"))
                .andExpect(jsonPath("$.data.user.name").value("Rohan Patel"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Invalid credentials returns HTTP 400 Bad Request via GlobalExceptionHandler")
    void testLoginInvalidCredentialsReturns400() throws Exception {
        LoginRequest request = new LoginRequest("rohan@example.com", "wrongPass");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadRequestException("Invalid email or password."));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid email or password."));
    }
}
