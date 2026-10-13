package com.smartinternship.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO for submitting a student internship application.
 *
 * SECURITY NOTE:
 * The student is securely resolved from the authenticated JWT token (SecurityContext),
 * rather than trusting any studentId provided in the request body.
 */
public class ApplicationRequest {

    // Optional: Ignored if authenticated user is present; always securely resolved from JWT
    private Long studentId;

    @NotNull(message = "Internship ID is required")
    private Long internshipId;

    @Size(max = 1000, message = "Notes/Cover letter cannot exceed 1000 characters")
    private String notes;

    @Pattern(regexp = "^(https?://.+)?$", message = "Resume URL must be a valid URL starting with http:// or https://")
    @Size(max = 500, message = "Resume URL cannot exceed 500 characters")
    private String resumeUrl;

    public ApplicationRequest() {}

    public ApplicationRequest(Long internshipId, String notes, String resumeUrl) {
        this.internshipId = internshipId;
        this.notes = notes;
        this.resumeUrl = resumeUrl;
    }

    public ApplicationRequest(Long studentId, Long internshipId, String notes, String resumeUrl) {
        this.studentId = studentId;
        this.internshipId = internshipId;
        this.notes = notes;
        this.resumeUrl = resumeUrl;
    }

    // --- Getters & Setters ---

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getInternshipId() {
        return internshipId;
    }

    public void setInternshipId(Long internshipId) {
        this.internshipId = internshipId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }
}
