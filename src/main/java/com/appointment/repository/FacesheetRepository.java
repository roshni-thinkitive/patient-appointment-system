package com.appointment.repository;

import com.appointment.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FacesheetRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUuidAndIsDeletedFalse(UUID uuid);
}
