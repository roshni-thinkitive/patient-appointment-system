package com.appointment.dto.ehr;

import com.appointment.enums.Severity;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AllergyDto {

    private UUID uuid;

    @NotBlank(message = "Substance is required")
    private String substance;

    private String reaction;

    private Severity severity;

    private LocalDate onsetDate;

    private LocalDateTime createdAt;
}
