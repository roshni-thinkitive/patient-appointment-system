package com.appointment.dto.patient;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class EmergencyContactDto {

    private UUID uuid;

    @NotBlank(message = "Contact name is required")
    private String contactName;

    private String relationship;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private String alternatePhone;

    private String email;
}
