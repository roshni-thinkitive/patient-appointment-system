package com.appointment.service;

import com.appointment.dto.ehr.AllergyDto;
import com.appointment.enums.Severity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AllergyService {

    AllergyDto createAllergy(UUID patientUuid, AllergyDto dto);

    AllergyDto updateAllergy(UUID uuid, AllergyDto dto);

    AllergyDto getAllergyByUuid(UUID uuid);

    void deleteAllergy(UUID uuid);

    Page<AllergyDto> getAllAllergies(UUID patientUuid, Severity severity, String searchString, Pageable pageable);
}
