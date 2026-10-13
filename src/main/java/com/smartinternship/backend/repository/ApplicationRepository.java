package com.smartinternship.backend.repository;

import com.smartinternship.backend.model.Application;
import com.smartinternship.backend.model.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    /**
     * Checks if a student has already applied for a specific internship.
     */
    boolean existsByStudentIdAndInternshipId(Long studentId, Long internshipId);

    Optional<Application> findByStudentIdAndInternshipId(Long studentId, Long internshipId);

    List<Application> findByStudentIdOrderByAppliedAtDesc(Long studentId);

    List<Application> findByInternshipIdOrderByAppliedAtDesc(Long internshipId);

    List<Application> findByStatus(ApplicationStatus status);

    long countByInternshipId(Long internshipId);

    long countByStudentId(Long studentId);
}
