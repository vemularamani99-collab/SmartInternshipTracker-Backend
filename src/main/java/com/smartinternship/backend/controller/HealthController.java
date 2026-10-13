package com.smartinternship.backend.controller;

import com.smartinternship.backend.dto.HealthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health Controller — Provides a simple verification endpoint.
 *
 * Endpoint: GET /api/health
 * Publicly accessible: Yes (does not require authentication)
 *
 * Used by:
 *   - Postman verification during initial setup
 *   - Docker/Kubernetes readiness probes
 *   - Frontend health checks
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> checkHealth() {
        HealthResponse response = new HealthResponse(
            "UP",
            "Smart Internship Tracker Backend API is running successfully!",
            "Smart Internship Tracker Service",
            "1.0.0"
        );
        return ResponseEntity.ok(response);
    }
}
