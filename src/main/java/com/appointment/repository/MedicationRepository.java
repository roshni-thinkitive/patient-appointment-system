package com.appointment.repository;

import com.appointment.entity.Medication;
import com.appointment.entity.Patient;
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
public interface MedicationRepository extends JpaRepository<Medication, Long> {

    Optional<Medication> findByUuidAndIsDeletedFalse(UUID uuid);

    @Query("SELECT m FROM Medication m WHERE m.isDeleted = false " +
           "AND (:patient IS NULL OR m.patient = :patient) " +
           "AND (:searchString IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', CAST(:searchString AS string), '%')))")
    Page<Medication> findAllFiltered(@Param("patient") Patient patient,
                                     @Param("searchString") String searchString,
                                     Pageable pageable);

    List<Medication> findTop5ByPatientAndIsDeletedFalseOrderByCreatedAtDesc(Patient patient);
}
