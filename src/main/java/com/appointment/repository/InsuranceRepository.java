package com.appointment.repository;

import com.appointment.entity.Insurance;
import com.appointment.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsuranceRepository extends JpaRepository<Insurance, Long> {

    List<Insurance> findAllByPatient(Patient patient);

    Optional<Insurance> findByPatientAndIsPrimaryTrue(Patient patient);
}
