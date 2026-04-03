package com.appointment.dto.patient;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PatientRegistrationDto {

    @NotNull(message = "Demographics are required")
    @Valid
    private DemographicsDto demographics;

    @Valid
    private ContactInfoDto contactInfo;

    @Valid
    private EmergencyContactDto emergencyContact;

    @Valid
    private InsuranceDto insurance;
}
