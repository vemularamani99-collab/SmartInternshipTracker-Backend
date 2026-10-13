package com.smartinternship.backend.service;

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
import com.smartinternship.backend.service.impl.ApplicationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ApplicationServiceImpl verifying application creation, duplicate checks,
 * status transitions, role enforcement, and application ownership security.
 */
@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InternshipRepository internshipRepository;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private User student1;
    private User student2;
    private User adminUser;
    private Internship internship;
    private Application application1;

    @BeforeEach
    void setUp() {
        student1 = new User();
        student1.setId(1L);
        student1.setEmail("student1@example.com");
        student1.setName("Alice Student");
        student1.setRole(Role.STUDENT);

        student2 = new User();
        student2.setId(2L);
        student2.setEmail("student2@example.com");
        student2.setName("Bob Student");
        student2.setRole(Role.STUDENT);

        adminUser = new User();
        adminUser.setId(99L);
        adminUser.setEmail("admin@example.com");
        adminUser.setName("Admin Officer");
        adminUser.setRole(Role.ADMIN);

        internship = new Internship();
        internship.setId(10L);
        internship.setTitle("Full Stack Engineer Intern");
        internship.setCompany("TechCorp");
        internship.setLocation("Bangalore");
        internship.setStipend(45000.0);

        application1 = new Application(student1, internship, "First round notes", "https://resume.com/alice.pdf");
        application1.setId(100L);
        application1.setStatus(ApplicationStatus.APPLIED);
        application1.setAppliedAt(LocalDateTime.now());
        application1.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("applyForInternship() - Successfully submits application for student")
    void testApplySuccess() {
        ApplicationRequest req = new ApplicationRequest(10L, "Excited to apply!", "https://resume.com/alice.pdf");

        when(userRepository.findByEmail("student1@example.com")).thenReturn(Optional.of(student1));
        when(internshipRepository.findById(10L)).thenReturn(Optional.of(internship));
        when(applicationRepository.existsByStudentIdAndInternshipId(1L, 10L)).thenReturn(false);
        when(applicationRepository.save(any(Application.class))).thenReturn(application1);

        ApplicationResponse res = applicationService.applyForInternship(req, "student1@example.com");

        assertNotNull(res);
        assertEquals(100L, res.getId());
        assertEquals("Full Stack Engineer Intern", res.getInternshipTitle());
        assertEquals("TechCorp", res.getCompany());
        assertEquals(ApplicationStatus.APPLIED, res.getStatus());
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    @DisplayName("applyForInternship() - Prevents duplicate application by same student")
    void testApplyDuplicateThrowsBadRequest() {
        ApplicationRequest req = new ApplicationRequest(10L, "Applying again", null);

        when(userRepository.findByEmail("student1@example.com")).thenReturn(Optional.of(student1));
        when(internshipRepository.findById(10L)).thenReturn(Optional.of(internship));
        when(applicationRepository.existsByStudentIdAndInternshipId(1L, 10L)).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
            applicationService.applyForInternship(req, "student1@example.com")
        );

        assertTrue(ex.getMessage().contains("already applied"));
        verify(applicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("applyForInternship() - Prevents Admin account from applying as student")
    void testApplyAsAdminThrowsAccessDenied() {
        ApplicationRequest req = new ApplicationRequest(10L, "Admin notes", null);

        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () ->
            applicationService.applyForInternship(req, "admin@example.com")
        );

        assertTrue(ex.getMessage().contains("STUDENT role"));
        verify(applicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("getMyApplications() - Fetches only the applications belonging to authenticated student")
    void testGetMyApplications() {
        when(userRepository.findByEmail("student1@example.com")).thenReturn(Optional.of(student1));
        when(applicationRepository.findByStudentIdOrderByAppliedAtDesc(1L)).thenReturn(List.of(application1));

        List<ApplicationResponse> list = applicationService.getMyApplications("student1@example.com");

        assertEquals(1, list.size());
        assertEquals("student1@example.com", list.get(0).getStudentEmail());
    }

    @Test
    @DisplayName("getApplicationById() - Denies student access to another student's application")
    void testStudentCannotViewOtherApplication() {
        when(applicationRepository.findById(100L)).thenReturn(Optional.of(application1)); // belongs to student1 (id: 1)
        when(userRepository.findByEmail("student2@example.com")).thenReturn(Optional.of(student2)); // student2 (id: 2)

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () ->
            applicationService.getApplicationById(100L, "student2@example.com")
        );

        assertTrue(ex.getMessage().contains("not authorized to view another student's application"));
    }

    @Test
    @DisplayName("getApplicationById() - Allows Admin to view any student's application")
    void testAdminCanViewAnyApplication() {
        when(applicationRepository.findById(100L)).thenReturn(Optional.of(application1));
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

        ApplicationResponse res = applicationService.getApplicationById(100L, "admin@example.com");

        assertNotNull(res);
        assertEquals(100L, res.getId());
    }

    @Test
    @DisplayName("updateApplicationStatus() - Allows student to update status from APPLIED to INTERVIEW")
    void testUpdateStatusSuccess() {
        when(applicationRepository.findById(100L)).thenReturn(Optional.of(application1));
        when(userRepository.findByEmail("student1@example.com")).thenReturn(Optional.of(student1));
        when(applicationRepository.save(any(Application.class))).thenAnswer(inv -> inv.getArgument(0));

        ApplicationResponse res = applicationService.updateApplicationStatus(
            100L, ApplicationStatus.INTERVIEW, "Interview on Friday", "student1@example.com"
        );

        assertEquals(ApplicationStatus.INTERVIEW, res.getStatus());
        assertEquals("Interview on Friday", res.getFeedback());
    }

    @Test
    @DisplayName("updateApplicationStatus() - Prevents student from reverting terminal SELECTED back to APPLIED")
    void testInvalidStatusReversionFromSelected() {
        application1.setStatus(ApplicationStatus.SELECTED);

        when(applicationRepository.findById(100L)).thenReturn(Optional.of(application1));
        when(userRepository.findByEmail("student1@example.com")).thenReturn(Optional.of(student1));

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
            applicationService.updateApplicationStatus(100L, ApplicationStatus.APPLIED, null, "student1@example.com")
        );

        assertTrue(ex.getMessage().contains("Cannot revert an offer received"));
    }

    @Test
    @DisplayName("withdrawApplication() - Allows student to withdraw their own application")
    void testWithdrawSuccess() {
        when(applicationRepository.findById(100L)).thenReturn(Optional.of(application1));
        when(userRepository.findByEmail("student1@example.com")).thenReturn(Optional.of(student1));

        applicationService.withdrawApplication(100L, "student1@example.com");

        verify(applicationRepository).delete(application1);
    }

    @Test
    @DisplayName("withdrawApplication() - Prevents student from withdrawing another student's application")
    void testWithdrawAnotherStudentThrowsAccessDenied() {
        when(applicationRepository.findById(100L)).thenReturn(Optional.of(application1)); // belongs to student1
        when(userRepository.findByEmail("student2@example.com")).thenReturn(Optional.of(student2)); // requested by student2

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () ->
            applicationService.withdrawApplication(100L, "student2@example.com")
        );

        assertTrue(ex.getMessage().contains("You can only withdraw your own applications"));
        verify(applicationRepository, never()).delete(any());
    }
}
