package com.appointment.dto.patient;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DemographicsDto {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private LocalDate dateOfBirth;

    private String gender;

    private String bloodGroup;

    private String maritalStatus;

    private String nationality;

    private String preferredLanguage;
}
