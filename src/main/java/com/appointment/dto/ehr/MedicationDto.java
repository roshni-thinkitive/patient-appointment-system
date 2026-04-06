package com.appointment.dto.ehr;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class MedicationDto {

    private UUID uuid;

    @NotBlank(message = "Medication name is required")
    private String name;

    private String dosage;

    private String frequency;

    private String route;

    private String prescriber;

    private LocalDate startDate;

    private LocalDateTime createdAt;
}
