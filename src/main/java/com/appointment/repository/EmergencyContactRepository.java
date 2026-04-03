package com.appointment.repository;

import com.appointment.entity.EmergencyContact;
import com.appointment.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long> {

    List<EmergencyContact> findAllByPatient(Patient patient);
}
