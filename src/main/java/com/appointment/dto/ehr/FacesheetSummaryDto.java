package com.appointment.dto.ehr;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FacesheetSummaryDto {

    private List<AllergyDto> allergies;
    private List<VitalsDto> vitals;
    private List<MedicationDto> medications;
    private List<HistoryDto> pastMedicalHistory;
    private List<HistoryDto> pastSurgicalHistory;
    private List<HistoryDto> familyHistory;
}
