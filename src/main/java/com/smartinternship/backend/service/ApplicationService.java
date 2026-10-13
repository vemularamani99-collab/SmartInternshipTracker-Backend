package com.smartinternship.backend.service;

import com.smartinternship.backend.dto.ApplicationRequest;
import com.smartinternship.backend.dto.ApplicationResponse;
import com.smartinternship.backend.model.ApplicationStatus;

import java.util.List;

public interface ApplicationService {

    /**
     * Submit an application using the authenticated student's email.
     */
    ApplicationResponse applyForInternship(ApplicationRequest request, String studentEmail);

    /**
     * Get all applications belonging to the authenticated student.
     */
    List<ApplicationResponse> getMyApplications(String studentEmail);

    /**
     * Get details of an application, verifying that a student can only view their own.
     */
    ApplicationResponse getApplicationById(Long id, String currentUserEmail);

    /**
     * Get all applications for a specific internship (Admin only).
     */
    List<ApplicationResponse> getApplicationsForInternship(Long internshipId);

    /**
     * Update application status and remarks according to designed workflow.
     */
    ApplicationResponse updateApplicationStatus(Long applicationId, ApplicationStatus status, String feedback, String currentUserEmail);

    /**
     * Withdraw an application, verifying ownership by the student.
     */
    void withdrawApplication(Long applicationId, String currentUserEmail);
}
