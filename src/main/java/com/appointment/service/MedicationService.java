package com.appointment.service;

import com.appointment.dto.ehr.MedicationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MedicationService {

    MedicationDto createMedication(UUID patientUuid, MedicationDto dto);

    MedicationDto updateMedication(UUID uuid, MedicationDto dto);

    MedicationDto getMedicationByUuid(UUID uuid);

    void deleteMedication(UUID uuid);

    Page<MedicationDto> getAllMedications(UUID patientUuid, String searchString, Pageable pageable);
}
