package com.smartinternship.backend.repository;

import com.smartinternship.backend.model.Internship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InternshipRepository extends JpaRepository<Internship, Long> {

    /**
     * Search internships by title, company, location, or tags with pagination.
     */
    @Query("SELECT i FROM Internship i WHERE " +
           "LOWER(i.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.company) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.location) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.tags) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Internship> searchInternships(@Param("search") String search, Pageable pageable);

    List<Internship> findByCompanyIgnoreCase(String company);

    List<Internship> findByLocationContainingIgnoreCase(String location);
}
