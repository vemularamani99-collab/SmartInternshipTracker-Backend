package com.smartinternship.backend.service;

import com.smartinternship.backend.dto.InternshipRequest;
import com.smartinternship.backend.dto.InternshipResponse;
import com.smartinternship.backend.dto.PaginatedResponse;
import com.smartinternship.backend.exception.ResourceNotFoundException;
import com.smartinternship.backend.model.Internship;
import com.smartinternship.backend.repository.ApplicationRepository;
import com.smartinternship.backend.repository.InternshipRepository;
import com.smartinternship.backend.service.impl.InternshipServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for InternshipServiceImpl verifying CRUD operations, pagination, and error handling.
 */
@ExtendWith(MockitoExtension.class)
class InternshipServiceTest {

    @Mock
    private InternshipRepository internshipRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private InternshipServiceImpl internshipService;

    private Internship internship;
    private InternshipRequest request;

    @BeforeEach
    void setUp() {
        internship = new Internship();
        internship.setId(50L);
        internship.setTitle("Backend Engineer Intern");
        internship.setCompany("CloudSystems");
        internship.setLocation("Hyderabad");
        internship.setStipend(50000.0);
        internship.setDuration("6 Months");
        internship.setDeadline(LocalDate.now().plusMonths(2));
        internship.setDescription("Develop Spring Boot microservices");
        internship.setInternshipType("Hybrid");
        internship.setOpenings(3);
        internship.setCreatedAt(LocalDateTime.now());
        internship.setUpdatedAt(LocalDateTime.now());

        request = new InternshipRequest();
        request.setTitle("Backend Engineer Intern");
        request.setCompany("CloudSystems");
        request.setLocation("Hyderabad");
        request.setStipend(50000.0);
        request.setDuration("6 Months");
        request.setDeadline(LocalDate.now().plusMonths(2));
        request.setDescription("Develop Spring Boot microservices");
        request.setInternshipType("Hybrid");
        request.setOpenings(3);
    }

    @Test
    @DisplayName("createInternship() - Creates and persists new internship")
    void testCreateInternshipSuccess() {
        when(internshipRepository.save(any(Internship.class))).thenReturn(internship);
        when(applicationRepository.countByInternshipId(50L)).thenReturn(0L);

        InternshipResponse res = internshipService.createInternship(request);

        assertNotNull(res);
        assertEquals(50L, res.getId());
        assertEquals("Backend Engineer Intern", res.getTitle());
        assertEquals("CloudSystems", res.getCompany());
        assertEquals(50000.0, res.getStipend());
        verify(internshipRepository).save(any(Internship.class));
    }

    @Test
    @DisplayName("getAllInternships() - Returns paginated list with total counts")
    void testGetAllInternshipsPagination() {
        Page<Internship> page = new PageImpl<>(List.of(internship));

        when(internshipRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(applicationRepository.countByInternshipId(50L)).thenReturn(4L);

        PaginatedResponse<InternshipResponse> response =
            internshipService.getAllInternships(0, 10, "id", "desc", null);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(1, response.getTotalElements());
        assertEquals(4, response.getContent().get(0).getApplicationCount());
    }

    @Test
    @DisplayName("getInternshipById() - Returns internship when found")
    void testGetInternshipByIdFound() {
        when(internshipRepository.findById(50L)).thenReturn(Optional.of(internship));
        when(applicationRepository.countByInternshipId(50L)).thenReturn(2L);

        InternshipResponse res = internshipService.getInternshipById(50L);

        assertNotNull(res);
        assertEquals("Backend Engineer Intern", res.getTitle());
        assertEquals(2, res.getApplicationCount());
    }

    @Test
    @DisplayName("getInternshipById() - Throws ResourceNotFoundException when ID does not exist")
    void testGetInternshipByIdNotFoundThrows() {
        when(internshipRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            internshipService.getInternshipById(999L)
        );
    }

    @Test
    @DisplayName("updateInternship() - Updates existing internship fields")
    void testUpdateInternshipSuccess() {
        when(internshipRepository.findById(50L)).thenReturn(Optional.of(internship));
        when(internshipRepository.save(any(Internship.class))).thenReturn(internship);

        request.setTitle("Senior Backend Engineer Intern");
        request.setStipend(60000.0);

        InternshipResponse res = internshipService.updateInternship(50L, request);

        assertNotNull(res);
        verify(internshipRepository).save(internship);
    }

    @Test
    @DisplayName("deleteInternship() - Deletes internship by ID")
    void testDeleteInternshipSuccess() {
        when(internshipRepository.findById(50L)).thenReturn(Optional.of(internship));

        internshipService.deleteInternship(50L);

        verify(internshipRepository).delete(internship);
    }
}
