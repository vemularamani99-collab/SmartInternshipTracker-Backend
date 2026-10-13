package com.smartinternship.backend.model;

/**
 * Roles for users within the Smart Internship Tracker application.
 *
 * Rules:
 *   - STUDENT: Publicly registered users who can browse internships and apply.
 *   - ADMIN: Placement coordinators/administrators who can post internships and review applications.
 */
public enum Role {
    STUDENT,
    ADMIN
}
