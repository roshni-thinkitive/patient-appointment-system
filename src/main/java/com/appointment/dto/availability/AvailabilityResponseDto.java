package com.appointment.dto.availability;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
public class AvailabilityResponseDto {

    private UUID slotUuid;
    private LocalTime startTime;
    private LocalTime endTime;
    private String timezone;
    private String location;
}
