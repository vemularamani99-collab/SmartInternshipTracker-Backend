package com.smartinternship.backend.controller;

import com.smartinternship.backend.dto.ApiResponse;
import com.smartinternship.backend.dto.ApplicationRequest;
import com.smartinternship.backend.dto.ApplicationResponse;
import com.smartinternship.backend.dto.ApplicationStatusUpdateRequest;
import com.smartinternship.backend.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Student Applications with strict ownership and role verification.
 *
 * Security Rules:
 *   - POST /api/applications: STUDENT only; student identity resolved from JWT.
 *   - GET /api/applications/my: STUDENT only; retrieves only the student's own applications.
 *   - GET /api/applications/{id}: Verified ownership (Student can only view their own; Admin can view any).
 *   - DELETE /api/applications/{id}: Verified ownership (Student can only withdraw their own).
 *   - GET /api/applications/internship/{id}: ADMIN only.
 *   - PATCH /api/applications/{id}/status: ADMIN only.
 */
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /**
     * Submit an application for an internship.
     * Accessible by: STUDENT
     * Ownership: Student identity is securely extracted from the authenticated JWT token.
     */
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<ApplicationResponse>> applyForInternship(
            @Valid @RequestBody ApplicationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ApplicationResponse response =
            applicationService.applyForInternship(request, userDetails.getUsername());
        return new ResponseEntity<>(
            ApiResponse.success("Application submitted successfully", response),
            HttpStatus.CREATED
        );
    }

    /**
     * Retrieve all applications submitted by the currently logged-in student.
     * Accessible by: STUDENT
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getMyApplications(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<ApplicationResponse> response =
            applicationService.getMyApplications(userDetails.getUsername());
        return ResponseEntity.ok(
            ApiResponse.success("My applications retrieved successfully", response)
        );
    }

    /**
     * Retrieve details of a specific application.
     * Accessible by: STUDENT (owner only) or ADMIN
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getApplicationById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        ApplicationResponse response =
            applicationService.getApplicationById(id, userDetails.getUsername());
        return ResponseEntity.ok(
            ApiResponse.success("Application details retrieved successfully", response)
        );
    }

    /**
     * Retrieve all applications received for a specific internship listing.
     * Accessible by: ADMIN
     */
    @GetMapping("/internship/{internshipId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getApplicationsForInternship(
            @PathVariable Long internshipId) {
        List<ApplicationResponse> response =
            applicationService.getApplicationsForInternship(internshipId);
        return ResponseEntity.ok(
            ApiResponse.success("Internship applicants retrieved successfully", response)
        );
    }

    /**
     * Update an application's status and remarks according to designed role workflow.
     * Accessible by: STUDENT (own application tracking) or ADMIN
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateApplicationStatus(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationStatusUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ApplicationResponse response =
            applicationService.updateApplicationStatus(id, request.getStatus(), request.getFeedback(), userDetails.getUsername());
        return ResponseEntity.ok(
            ApiResponse.success("Application status updated successfully", response)
        );
    }

    /**
     * Withdraw an application.
     * Accessible by: STUDENT (owner only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> withdrawApplication(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        applicationService.withdrawApplication(id, userDetails.getUsername());
        return ResponseEntity.ok(
            ApiResponse.success("Application withdrawn successfully", null)
        );
    }
}
