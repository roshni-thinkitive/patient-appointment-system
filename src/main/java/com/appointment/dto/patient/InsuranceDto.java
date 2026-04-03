package com.appointment.dto.patient;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class InsuranceDto {

    private UUID uuid;

    @NotBlank(message = "Insurance name is required")
    private String insuranceName;

    private String payerId;

    @NotBlank(message = "Member ID is required")
    private String memberId;

    private String groupNumber;

    private String policyHolderName;

    private String relationship;

    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "isPrimary is required")
    private Boolean isPrimary;
}
