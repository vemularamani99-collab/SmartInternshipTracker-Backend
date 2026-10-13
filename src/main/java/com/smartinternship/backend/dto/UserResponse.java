package com.smartinternship.backend.dto;

import com.smartinternship.backend.model.Role;

import java.time.LocalDateTime;

/**
 * Sanitized user details DTO returned in API responses.
 *
 * SECURITY GUARANTEE: Never exposes password or password hashes.
 */
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private String college;
    private String degree;
    private Integer graduationYear;
    private LocalDateTime createdAt;

    public UserResponse() {}

    public UserResponse(Long id, String name, String email, Role role, String college, String degree, Integer graduationYear, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.college = college;
        this.degree = degree;
        this.graduationYear = graduationYear;
        this.createdAt = createdAt;
    }

    // --- Getters & Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public Integer getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
