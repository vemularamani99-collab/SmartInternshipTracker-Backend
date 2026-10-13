package com.smartinternship.backend.controller;

import com.smartinternship.backend.dto.ApiResponse;
import com.smartinternship.backend.dto.InternshipRequest;
import com.smartinternship.backend.dto.InternshipResponse;
import com.smartinternship.backend.dto.PaginatedResponse;
import com.smartinternship.backend.service.InternshipService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Internship Listings.
 *
 * Security Rules:
 *   - Public (Unauthenticated & Students): GET browsing & searching
 *   - ADMIN only: POST, PUT, DELETE operations
 */
@RestController
@RequestMapping("/api/internships")
public class InternshipController {

    private final InternshipService internshipService;

    public InternshipController(InternshipService internshipService) {
        this.internshipService = internshipService;
    }

    /**
     * Create a new internship listing.
     * Accessible by: ADMIN only
     * HTTP 201 Created (or 403 Forbidden for students/guests)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InternshipResponse>> createInternship(
            @Valid @RequestBody InternshipRequest request) {
        InternshipResponse response = internshipService.createInternship(request);
        return new ResponseEntity<>(
            ApiResponse.success("Internship created successfully", response),
            HttpStatus.CREATED
        );
    }

    /**
     * Retrieve all internships with pagination, sorting, and optional keyword search.
     * Accessible by: Public (Guests, Students, Admins)
     * HTTP 200 OK
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<InternshipResponse>>> getAllInternships(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search) {
        PaginatedResponse<InternshipResponse> response =
            internshipService.getAllInternships(page, size, sortBy, sortDir, search);
        return ResponseEntity.ok(
            ApiResponse.success("Internships fetched successfully", response)
        );
    }

    /**
     * Retrieve a specific internship by ID.
     * Accessible by: Public
     * HTTP 200 OK (or 404 Not Found)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InternshipResponse>> getInternshipById(@PathVariable Long id) {
        InternshipResponse response = internshipService.getInternshipById(id);
        return ResponseEntity.ok(
            ApiResponse.success("Internship retrieved successfully", response)
        );
    }

    /**
     * Update an internship listing by ID.
     * Accessible by: ADMIN only
     * HTTP 200 OK (or 403 Forbidden)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InternshipResponse>> updateInternship(
            @PathVariable Long id,
            @Valid @RequestBody InternshipRequest request) {
        InternshipResponse response = internshipService.updateInternship(id, request);
        return ResponseEntity.ok(
            ApiResponse.success("Internship updated successfully", response)
        );
    }

    /**
     * Delete an internship listing by ID.
     * Accessible by: ADMIN only
     * HTTP 200 OK (or 403 Forbidden)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteInternship(@PathVariable Long id) {
        internshipService.deleteInternship(id);
        return ResponseEntity.ok(
            ApiResponse.success("Internship listing deleted successfully", null)
        );
    }
}
