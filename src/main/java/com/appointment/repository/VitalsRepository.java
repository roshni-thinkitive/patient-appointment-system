package com.appointment.repository;

import com.appointment.entity.Patient;
import com.appointment.entity.Vitals;
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
public interface VitalsRepository extends JpaRepository<Vitals, Long> {

    Optional<Vitals> findByUuidAndIsDeletedFalse(UUID uuid);

    @Query("SELECT v FROM Vitals v WHERE v.isDeleted = false " +
           "AND (:patient IS NULL OR v.patient = :patient)")
    Page<Vitals> findAllFiltered(@Param("patient") Patient patient, Pageable pageable);

    List<Vitals> findTop3ByPatientAndIsDeletedFalseOrderByCreatedAtDesc(Patient patient);
}
