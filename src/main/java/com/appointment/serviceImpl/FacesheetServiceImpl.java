package com.appointment.serviceImpl;

import com.appointment.dto.ehr.*;
import com.appointment.enums.HistoryType;
import com.appointment.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FacesheetServiceImpl implements FacesheetService {

    private final AllergyService allergyService;
    private final VitalsService vitalsService;
    private final MedicationService medicationService;
    private final HistoryService historyService;

    @Override
    @Transactional(readOnly = true)
    public FacesheetSummaryDto getFacesheet(UUID patientUuid) {
        Pageable top5 = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        Pageable top3 = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<AllergyDto> allergies = allergyService.getAllAllergies(patientUuid, null, null, top5).getContent();
        List<VitalsDto> vitals = vitalsService.getAllVitals(patientUuid, top3).getContent();
        List<MedicationDto> medications = medicationService.getAllMedications(patientUuid, null, top5).getContent();

        Pageable allRecords = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<HistoryDto> pastMedicalHistory = historyService
                .getAllHistories(patientUuid, HistoryType.PAST_MEDICAL, null, null, allRecords).getContent();
        List<HistoryDto> pastSurgicalHistory = historyService
                .getAllHistories(patientUuid, HistoryType.PAST_SURGICAL, null, null, allRecords).getContent();
        List<HistoryDto> familyHistory = historyService
                .getAllHistories(patientUuid, HistoryType.FAMILY, null, null, allRecords).getContent();

        return FacesheetSummaryDto.builder()
                .allergies(allergies)
                .vitals(vitals)
                .medications(medications)
                .pastMedicalHistory(pastMedicalHistory)
                .pastSurgicalHistory(pastSurgicalHistory)
                .familyHistory(familyHistory)
                .build();
    }
}
