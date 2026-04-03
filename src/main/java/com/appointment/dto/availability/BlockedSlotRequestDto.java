package com.appointment.dto.availability;

import com.appointment.enums.BlockType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BlockedSlotRequestDto {

    private BlockType blockType;

    @NotNull(message = "Date is required")
    private LocalDate date;

    private Boolean blockEntireDay = false;

    private LocalTime startTime;

    private LocalTime endTime;

    private String notes;
}
