package com.appointment.dto.availability;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class BlockedSlotRequestDto {

    @NotNull(message = "Provider UUID is required")
    private UUID providerUuid;

    @NotNull(message = "Blocked date is required")
    private LocalDate blockedDate;

    @NotNull(message = "Block start time is required")
    private LocalTime blockStartTime;

    @NotNull(message = "Block end time is required")
    private LocalTime blockEndTime;

    private String reason;
}
