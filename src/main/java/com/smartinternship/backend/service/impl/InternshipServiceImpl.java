package com.smartinternship.backend.service.impl;

import com.smartinternship.backend.dto.InternshipRequest;
import com.smartinternship.backend.dto.InternshipResponse;
import com.smartinternship.backend.dto.PaginatedResponse;
import com.smartinternship.backend.exception.ResourceNotFoundException;
import com.smartinternship.backend.model.Internship;
import com.smartinternship.backend.repository.ApplicationRepository;
import com.smartinternship.backend.repository.InternshipRepository;
import com.smartinternship.backend.service.InternshipService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InternshipServiceImpl implements InternshipService {

    private final InternshipRepository internshipRepository;
    private final ApplicationRepository applicationRepository;

    public InternshipServiceImpl(InternshipRepository internshipRepository, ApplicationRepository applicationRepository) {
        this.internshipRepository = internshipRepository;
        this.applicationRepository = applicationRepository;
    }

    @Override
    public InternshipResponse createInternship(InternshipRequest request) {
        Internship internship = new Internship();
        mapRequestToEntity(request, internship);

        Internship saved = internshipRepository.save(internship);
        return mapEntityToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<InternshipResponse> getAllInternships(int page, int size, String sortBy, String sortDir, String search) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);

        Page<Internship> internshipPage;
        if (search != null && !search.trim().isEmpty()) {
            internshipPage = internshipRepository.searchInternships(search.trim(), pageable);
        } else {
            internshipPage = internshipRepository.findAll(pageable);
        }

        List<InternshipResponse> responseList = internshipPage.getContent().stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());

        return new PaginatedResponse<>(
                responseList,
                internshipPage.getNumber(),
                internshipPage.getSize(),
                internshipPage.getTotalElements(),
                internshipPage.getTotalPages(),
                internshipPage.isLast(),
                internshipPage.isFirst()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public InternshipResponse getInternshipById(Long id) {
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Internship", "id", id));
        return mapEntityToResponse(internship);
    }

    @Override
    public InternshipResponse updateInternship(Long id, InternshipRequest request) {
        Internship existing = internshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Internship", "id", id));

        mapRequestToEntity(request, existing);

        Internship updated = internshipRepository.save(existing);
        return mapEntityToResponse(updated);
    }

    @Override
    public void deleteInternship(Long id) {
        Internship existing = internshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Internship", "id", id));
        internshipRepository.delete(existing);
    }

    // --- Helper Mappers ---

    private void mapRequestToEntity(InternshipRequest request, Internship entity) {
        entity.setTitle(request.getTitle());
        entity.setCompany(request.getCompany());
        entity.setLocation(request.getLocation());
        entity.setStipend(request.getStipend());
        entity.setDuration(request.getDuration());
        entity.setDeadline(request.getDeadline());
        entity.setLogo(request.getLogo());
        entity.setTags(request.getTags());
        entity.setDescription(request.getDescription());
        entity.setRequirements(request.getRequirements());
        entity.setApplicationUrl(request.getApplicationUrl());
        entity.setInternshipType(request.getInternshipType() != null ? request.getInternshipType() : "Full-time");
        entity.setOpenings(request.getOpenings() != null ? request.getOpenings() : 1);
    }

    private InternshipResponse mapEntityToResponse(Internship entity) {
        InternshipResponse response = new InternshipResponse();
        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setCompany(entity.getCompany());
        response.setLocation(entity.getLocation());
        response.setStipend(entity.getStipend());
        response.setDuration(entity.getDuration());
        response.setDeadline(entity.getDeadline());
        response.setLogo(entity.getLogo());
        response.setTags(entity.getTags());
        response.setDescription(entity.getDescription());
        response.setRequirements(entity.getRequirements());
        response.setApplicationUrl(entity.getApplicationUrl());
        response.setInternshipType(entity.getInternshipType());
        response.setOpenings(entity.getOpenings());
        response.setApplicationCount((int) applicationRepository.countByInternshipId(entity.getId()));
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}
