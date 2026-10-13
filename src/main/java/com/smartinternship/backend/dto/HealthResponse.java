package com.smartinternship.backend.dto;

import java.time.LocalDateTime;

/**
 * DTO for the /api/health endpoint response.
 */
public class HealthResponse {

    private String status;
    private String message;
    private String service;
    private String version;
    private LocalDateTime timestamp;

    public HealthResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public HealthResponse(String status, String message, String service, String version) {
        this.status = status;
        this.message = message;
        this.service = service;
        this.version = version;
        this.timestamp = LocalDateTime.now();
    }

    // --- Getters and Setters ---

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
