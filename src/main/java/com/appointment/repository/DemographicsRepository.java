package com.appointment.repository;

import com.appointment.entity.Demographics;
import com.appointment.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DemographicsRepository extends JpaRepository<Demographics, Long> {

    Optional<Demographics> findByPatient(Patient patient);
}
