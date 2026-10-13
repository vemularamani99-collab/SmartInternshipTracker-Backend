package com.smartinternship.backend.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO for creating and updating internship listings.
 */
public class InternshipRequest {

    @NotBlank(message = "Internship title cannot be blank")
    @Size(min = 3, max = 150, message = "Title must be between 3 and 150 characters")
    private String title;

    @NotBlank(message = "Company name cannot be blank")
    @Size(min = 2, max = 150, message = "Company name must be between 2 and 150 characters")
    private String company;

    @NotBlank(message = "Location cannot be blank")
    @Size(max = 100, message = "Location cannot exceed 100 characters")
    private String location;

    @PositiveOrZero(message = "Stipend must be positive or zero")
    @Max(value = 5000000, message = "Stipend value cannot exceed sensible limit (₹50,00,000/mo)")
    private Double stipend;

    @Size(max = 50, message = "Duration cannot exceed 50 characters")
    private String duration;

    @FutureOrPresent(message = "Application deadline must be today or a future date")
    private LocalDate deadline;

    private String logo;

    private String tags;

    @NotBlank(message = "Job description cannot be blank")
    private String description;

    private String requirements;

    @Pattern(regexp = "^(https?://.+)?$", message = "Application URL must be a valid URL starting with http:// or https://")
    @Size(max = 500, message = "Application URL cannot exceed 500 characters")
    private String applicationUrl;

    @Size(max = 50, message = "Internship type cannot exceed 50 characters")
    private String internshipType;

    @Min(value = 1, message = "Number of openings must be at least 1")
    private Integer openings = 1;

    public InternshipRequest() {}

    // --- Getters & Setters ---

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getStipend() {
        return stipend;
    }

    public void setStipend(Double stipend) {
        this.stipend = stipend;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getApplicationUrl() {
        return applicationUrl;
    }

    public void setApplicationUrl(String applicationUrl) {
        this.applicationUrl = applicationUrl;
    }

    public String getInternshipType() {
        return internshipType;
    }

    public void setInternshipType(String internshipType) {
        this.internshipType = internshipType;
    }

    public Integer getOpenings() {
        return openings;
    }

    public void setOpenings(Integer openings) {
        this.openings = openings;
    }
}
