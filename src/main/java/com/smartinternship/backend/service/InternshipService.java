package com.smartinternship.backend.service;

import com.smartinternship.backend.dto.InternshipRequest;
import com.smartinternship.backend.dto.InternshipResponse;
import com.smartinternship.backend.dto.PaginatedResponse;

public interface InternshipService {

    InternshipResponse createInternship(InternshipRequest request);

    PaginatedResponse<InternshipResponse> getAllInternships(int page, int size, String sortBy, String sortDir, String search);

    InternshipResponse getInternshipById(Long id);

    InternshipResponse updateInternship(Long id, InternshipRequest request);

    void deleteInternship(Long id);
}
