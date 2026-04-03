package com.appointment.dto.patient;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PatientResponseDto {

    private UUID uuid;

    private String createdBy;

    private DemographicsDto demographics;

    private ContactInfoDto contactInfo;

    private List<EmergencyContactDto> emergencyContacts;

    private List<InsuranceDto> insurances;

    private LocalDateTime createdAt;
}
