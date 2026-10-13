package com.smartinternship.backend.service.impl;

import com.smartinternship.backend.dto.ApplicationRequest;
import com.smartinternship.backend.dto.ApplicationResponse;
import com.smartinternship.backend.exception.BadRequestException;
import com.smartinternship.backend.exception.ResourceNotFoundException;
import com.smartinternship.backend.model.Application;
import com.smartinternship.backend.model.ApplicationStatus;
import com.smartinternship.backend.model.Internship;
import com.smartinternship.backend.model.Role;
import com.smartinternship.backend.model.User;
import com.smartinternship.backend.repository.ApplicationRepository;
import com.smartinternship.backend.repository.InternshipRepository;
import com.smartinternship.backend.repository.UserRepository;
import com.smartinternship.backend.service.ApplicationService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final InternshipRepository internshipRepository;

    public ApplicationServiceImpl(ApplicationRepository applicationRepository,
                                  UserRepository userRepository,
                                  InternshipRepository internshipRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.internshipRepository = internshipRepository;
    }

    @Override
    public ApplicationResponse applyForInternship(ApplicationRequest request, String studentEmail) {
        // 1. Resolve student strictly from the authenticated identity
        User student = userRepository.findByEmail(studentEmail.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User/Student", "email", studentEmail));

        // 2. Ensure only STUDENTS can apply
        if (student.getRole() != Role.STUDENT) {
            throw new AccessDeniedException("Only accounts with the STUDENT role can apply for internships.");
        }

        // 3. Verify internship exists
        Internship internship = internshipRepository.findById(request.getInternshipId())
                .orElseThrow(() -> new ResourceNotFoundException("Internship", "id", request.getInternshipId()));

        // 4. Prevent duplicate applications by the same student for the same internship
        if (applicationRepository.existsByStudentIdAndInternshipId(student.getId(), internship.getId())) {
            throw new BadRequestException(String.format(
                "You have already applied for '%s' at %s. Duplicate applications are not allowed.",
                internship.getTitle(), internship.getCompany()
            ));
        }

        // 5. Create and persist application
        Application application = new Application(
            student,
            internship,
            request.getNotes(),
            request.getResumeUrl()
        );

        Application saved = applicationRepository.save(application);
        return mapEntityToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getMyApplications(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User/Student", "email", studentEmail));

        return applicationRepository.findByStudentIdOrderByAppliedAtDesc(student.getId()).stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationById(Long id, String currentUserEmail) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", id));

        User currentUser = userRepository.findByEmail(currentUserEmail.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", currentUserEmail));

        // OWNERSHIP ENFORCEMENT:
        // A student can ONLY view their own application. Admins can view any application.
        if (currentUser.getRole() == Role.STUDENT &&
            !application.getStudent().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Access denied: You are not authorized to view another student's application.");
        }

        return mapEntityToResponse(application);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsForInternship(Long internshipId) {
        if (!internshipRepository.existsById(internshipId)) {
            throw new ResourceNotFoundException("Internship", "id", internshipId);
        }
        return applicationRepository.findByInternshipIdOrderByAppliedAtDesc(internshipId).stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ApplicationResponse updateApplicationStatus(Long applicationId, ApplicationStatus status, String feedback, String currentUserEmail) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));

        User currentUser = userRepository.findByEmail(currentUserEmail.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", currentUserEmail));

        // WORKFLOW & OWNERSHIP VALIDATION:
        if (currentUser.getRole() == Role.STUDENT) {
            // 1. Verify ownership: student can ONLY update their own application
            if (!application.getStudent().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Access denied: You cannot modify another student's application records.");
            }

            // 2. Validate student tracking workflow transitions:
            // Permitted student tracking statuses: APPLIED, INTERVIEW, SELECTED, REJECTED, UNDER_REVIEW
            ApplicationStatus currentStatus = application.getStatus();

            // Terminal states cannot be reverted to earlier pipeline stages by student
            if ((currentStatus == ApplicationStatus.SELECTED || currentStatus == ApplicationStatus.ACCEPTED) &&
                (status == ApplicationStatus.APPLIED || status == ApplicationStatus.UNDER_REVIEW)) {
                throw new BadRequestException(String.format(
                    "Invalid workflow transition: Cannot revert an offer received (%s) back to %s.",
                    currentStatus, status
                ));
            }

            if (currentStatus == ApplicationStatus.REJECTED &&
                (status == ApplicationStatus.APPLIED || status == ApplicationStatus.UNDER_REVIEW)) {
                throw new BadRequestException("Invalid workflow transition: Cannot reopen a closed/rejected application back to applied.");
            }
        }

        application.setStatus(status);
        if (feedback != null && !feedback.isBlank()) {
            application.setFeedback(feedback.trim());
        }

        Application updated = applicationRepository.save(application);
        return mapEntityToResponse(updated);
    }

    @Override
    public void withdrawApplication(Long applicationId, String currentUserEmail) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));

        User currentUser = userRepository.findByEmail(currentUserEmail.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", currentUserEmail));

        // OWNERSHIP ENFORCEMENT:
        // Students can ONLY withdraw their own application.
        if (currentUser.getRole() == Role.STUDENT &&
            !application.getStudent().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Access denied: You can only withdraw your own applications.");
        }

        applicationRepository.delete(application);
    }

    // --- Helper Mapper ---

    private ApplicationResponse mapEntityToResponse(Application entity) {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(entity.getId());
        response.setStudentId(entity.getStudent().getId());
        response.setStudentName(entity.getStudent().getName());
        response.setStudentEmail(entity.getStudent().getEmail());
        response.setInternshipId(entity.getInternship().getId());
        response.setInternshipTitle(entity.getInternship().getTitle());
        response.setCompany(entity.getInternship().getCompany());
        response.setLocation(entity.getInternship().getLocation());
        response.setStipend(entity.getInternship().getStipend());
        response.setStatus(entity.getStatus());
        response.setNotes(entity.getNotes());
        response.setResumeUrl(entity.getResumeUrl());
        response.setFeedback(entity.getFeedback());
        response.setAppliedAt(entity.getAppliedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}
