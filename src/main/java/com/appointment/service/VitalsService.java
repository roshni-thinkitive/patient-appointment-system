package com.appointment.service;

import com.appointment.dto.ehr.VitalsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface VitalsService {

    VitalsDto createVitals(UUID patientUuid, VitalsDto dto);

    VitalsDto updateVitals(UUID uuid, VitalsDto dto);

    VitalsDto getVitalsByUuid(UUID uuid);

    void deleteVitals(UUID uuid);

    Page<VitalsDto> getAllVitals(UUID patientUuid, Pageable pageable);
}
