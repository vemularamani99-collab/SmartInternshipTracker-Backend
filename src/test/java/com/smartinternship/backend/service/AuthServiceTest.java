package com.smartinternship.backend.service;

import com.smartinternship.backend.dto.AuthResponse;
import com.smartinternship.backend.dto.LoginRequest;
import com.smartinternship.backend.dto.RegisterRequest;
import com.smartinternship.backend.exception.BadRequestException;
import com.smartinternship.backend.model.Role;
import com.smartinternship.backend.model.User;
import com.smartinternship.backend.repository.UserRepository;
import com.smartinternship.backend.security.JwtUtils;
import com.smartinternship.backend.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthServiceImpl using JUnit 5 and Mockito.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User studentUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Priya Sharma");
        registerRequest.setEmail("priya@example.com");
        registerRequest.setPassword("securePass123");
        registerRequest.setCollege("IIT Bombay");
        registerRequest.setDegree("B.Tech CSE");
        registerRequest.setGraduationYear(2026);

        loginRequest = new LoginRequest("priya@example.com", "securePass123");

        studentUser = new User();
        studentUser.setId(101L);
        studentUser.setName("Priya Sharma");
        studentUser.setEmail("priya@example.com");
        studentUser.setPassword("$2a$10$hashedPasswordPriya");
        studentUser.setRole(Role.STUDENT);
        studentUser.setCollege("IIT Bombay");
        studentUser.setDegree("B.Tech CSE");
        studentUser.setGraduationYear(2026);
        studentUser.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("register() - Successfully registers a student with encrypted password and STUDENT role")
    void testRegisterSuccess() {
        when(userRepository.existsByEmail("priya@example.com")).thenReturn(false);
        when(passwordEncoder.encode("securePass123")).thenReturn("$2a$10$hashedPasswordPriya");
        when(userRepository.save(any(User.class))).thenReturn(studentUser);
        when(jwtUtils.generateToken(any(User.class))).thenReturn("mocked.jwt.token");
        when(jwtUtils.getExpirationMs()).thenReturn(86400000L);

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.getToken());
        assertEquals("priya@example.com", response.getUser().getEmail());
        assertEquals(Role.STUDENT, response.getUser().getRole());

        verify(userRepository).existsByEmail("priya@example.com");
        verify(passwordEncoder).encode("securePass123");
        verify(userRepository).save(argThat(user ->
            user.getEmail().equals("priya@example.com") &&
            user.getRole() == Role.STUDENT &&
            user.getPassword().equals("$2a$10$hashedPasswordPriya")
        ));
    }

    @Test
    @DisplayName("register() - Fails with BadRequestException when email already exists")
    void testRegisterDuplicateEmailThrowsBadRequest() {
        when(userRepository.existsByEmail("priya@example.com")).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
            authService.register(registerRequest)
        );

        assertTrue(ex.getMessage().contains("already exists"));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("login() - Successfully authenticates user and returns JWT token")
    void testLoginSuccess() {
        when(userRepository.findByEmail("priya@example.com")).thenReturn(Optional.of(studentUser));
        when(passwordEncoder.matches("securePass123", studentUser.getPassword())).thenReturn(true);
        when(jwtUtils.generateToken(studentUser)).thenReturn("login.jwt.token");
        when(jwtUtils.getExpirationMs()).thenReturn(86400000L);

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("login.jwt.token", response.getToken());
        assertEquals("Priya Sharma", response.getUser().getName());
        assertEquals(Role.STUDENT, response.getUser().getRole());
    }

    @Test
    @DisplayName("login() - Fails with BadRequestException when password is incorrect")
    void testLoginIncorrectPasswordThrowsBadRequest() {
        when(userRepository.findByEmail("priya@example.com")).thenReturn(Optional.of(studentUser));
        when(passwordEncoder.matches("wrongPass", studentUser.getPassword())).thenReturn(false);

        LoginRequest wrongReq = new LoginRequest("priya@example.com", "wrongPass");

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
            authService.login(wrongReq)
        );

        assertEquals("Invalid email or password.", ex.getMessage());
        verify(jwtUtils, never()).generateToken(any());
    }

    @Test
    @DisplayName("login() - Fails with BadRequestException when email is not found")
    void testLoginUserNotFoundThrowsBadRequest() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        LoginRequest unknownReq = new LoginRequest("unknown@example.com", "anyPass");

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
            authService.login(unknownReq)
        );

        assertEquals("Invalid email or password.", ex.getMessage());
    }
}
