package com.appointment.service;

import com.appointment.dto.patient.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PatientService {

    PatientResponseDto createPatient(PatientRegistrationDto dto, String email);

    PatientResponseDto getPatientByUuid(UUID uuid);

    PatientResponseDto updateDemographics(UUID uuid, DemographicsDto dto);

    PatientResponseDto updateContactInfo(UUID uuid, ContactInfoDto dto);

    EmergencyContactDto addEmergencyContact(UUID uuid, EmergencyContactDto dto);

    InsuranceDto addInsurance(UUID uuid, InsuranceDto dto);

    Page<PatientResponseDto> getAllPatients(Pageable pageable);
}
