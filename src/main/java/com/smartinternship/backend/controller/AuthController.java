package com.smartinternship.backend.controller;

import com.smartinternship.backend.dto.ApiResponse;
import com.smartinternship.backend.dto.AuthResponse;
import com.smartinternship.backend.dto.LoginRequest;
import com.smartinternship.backend.dto.RegisterRequest;
import com.smartinternship.backend.dto.UserResponse;
import com.smartinternship.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication REST Controller.
 *
 * Endpoints:
 *   POST /api/auth/register - Register a new student account (always assigns STUDENT)
 *   POST /api/auth/login    - Authenticate user credentials and return signed JWT
 *   GET  /api/auth/me       - Retrieve current authenticated user profile
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Public Student Registration endpoint.
     * Enforces Role.STUDENT assignment on server side.
     * HTTP 201 Created
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(
            ApiResponse.success("Account registered successfully as Student", response),
            HttpStatus.CREATED
        );
    }

    /**
     * User Login endpoint for both Students and Admins.
     * HTTP 200 OK
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(
            ApiResponse.success("Authentication successful", response)
        );
    }

    /**
     * Retrieve the profile of the currently logged-in user via token claims.
     * HTTP 200 OK
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User is not authenticated"));
        }
        UserResponse profile = authService.getCurrentUserProfile(userDetails.getUsername());
        return ResponseEntity.ok(
            ApiResponse.success("Profile retrieved successfully", profile)
        );
    }
}
