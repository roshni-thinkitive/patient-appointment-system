package com.appointment.dto.availability;

import com.appointment.enums.BlockType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
public class BlockedSlotResponseDto {

    private UUID blockUuid;
    private BlockType blockType;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean blockEntireDay;
    private String notes;
}
