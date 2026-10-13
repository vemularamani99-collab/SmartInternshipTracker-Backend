package com.smartinternship.backend.controller;

import com.smartinternship.backend.dto.ApiResponse;
import com.smartinternship.backend.dto.UserResponse;
import com.smartinternship.backend.model.Role;
import com.smartinternship.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Administrator User Management.
 *
 * Security: Restricted strictly to ADMIN role.
 * Endpoint:
 *   GET /api/admin/users         - View all registered users (optional ?role=STUDENT filter)
 *   GET /api/admin/users/{id}    - View single registered user profile
 */
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieve all registered users.
     * Accessible by: ADMIN only
     * HTTP 200 OK (or 403 Forbidden for non-admins)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
            @RequestParam(required = false) Role role) {
        List<UserResponse> users = userService.getAllUsers(role);
        return ResponseEntity.ok(
            ApiResponse.success("Users retrieved successfully", users)
        );
    }

    /**
     * Retrieve single registered user profile by ID.
     * Accessible by: ADMIN only
     * HTTP 200 OK (or 404 Not Found)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(
            ApiResponse.success("User retrieved successfully", user)
        );
    }
}
