package com.smartinternship.backend.model;

/**
 * Status of a student's internship application.
 *
 * Supported workflow states:
 *   - APPLIED: Submitted by student, awaiting initial screening
 *   - UNDER_REVIEW: In review by recruiting team
 *   - INTERVIEW: Student scheduled/completed interview stage
 *   - SELECTED: Student selected for the internship offer
 *   - ACCEPTED: Offer accepted state
 *   - REJECTED: Application closed/rejected
 */
public enum ApplicationStatus {
    APPLIED,
    UNDER_REVIEW,
    INTERVIEW,
    SELECTED,
    ACCEPTED,
    REJECTED
}

