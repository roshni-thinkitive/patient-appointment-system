package com.appointment.repository;

import com.appointment.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUuid(UUID uuid);

    Optional<Patient> findByUuidAndIsDeletedFalse(UUID uuid);

    Page<Patient> findAllByIsDeletedFalse(Pageable pageable);

    List<Patient> findAllByCreatedBy(String email);
}
