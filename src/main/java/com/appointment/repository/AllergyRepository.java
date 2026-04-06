package com.appointment.repository;

import com.appointment.entity.Allergy;
import com.appointment.entity.Patient;
import com.appointment.enums.Severity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AllergyRepository extends JpaRepository<Allergy, Long> {

    Optional<Allergy> findByUuidAndIsDeletedFalse(UUID uuid);

    @Query("SELECT a FROM Allergy a WHERE a.isDeleted = false " +
           "AND (:patient IS NULL OR a.patient = :patient) " +
           "AND (:severity IS NULL OR a.severity = :severity) " +
           "AND (:searchString IS NULL OR LOWER(a.substance) LIKE LOWER(CONCAT('%', CAST(:searchString AS string), '%')))")
    Page<Allergy> findAllFiltered(@Param("patient") Patient patient,
                                  @Param("severity") Severity severity,
                                  @Param("searchString") String searchString,
                                  Pageable pageable);

    List<Allergy> findTop5ByPatientAndIsDeletedFalseOrderByCreatedAtDesc(Patient patient);
}
