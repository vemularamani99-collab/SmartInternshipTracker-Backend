package com.smartinternship.backend.dto;

import com.smartinternship.backend.model.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for administrators updating an application's status and remarks.
 */
public class ApplicationStatusUpdateRequest {

    @NotNull(message = "Application status cannot be null")
    private ApplicationStatus status;

    private String feedback;

    public ApplicationStatusUpdateRequest() {}

    public ApplicationStatusUpdateRequest(ApplicationStatus status, String feedback) {
        this.status = status;
        this.feedback = feedback;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
