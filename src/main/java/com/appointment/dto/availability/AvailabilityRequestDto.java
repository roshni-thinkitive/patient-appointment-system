package com.appointment.dto.availability;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Data
public class AvailabilityRequestDto {

    @NotNull(message = "Weekly schedule is required")
    @Valid
    private List<DaySchedule> weeklySchedule;

    @Data
    public static class DaySchedule {
        @NotNull(message = "Day of week is required")
        private DayOfWeek dayOfWeek;

        @NotNull(message = "Slots list is required")
        @Valid
        private List<Slot> slots;
    }

    @Data
    public static class Slot {
        @NotNull(message = "Start time is required")
        private LocalTime startTime;

        @NotNull(message = "End time is required")
        private LocalTime endTime;

        private String timezone;

        private String location;
    }
}
