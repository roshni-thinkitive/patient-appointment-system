package com.appointment.dto.availability;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
public class BlockedSlotResponseDto {

    private UUID uuid;
    private LocalDate blockedDate;
    private LocalTime blockStartTime;
    private LocalTime blockEndTime;
    private String reason;
}
