package com.appointment.dto.availability;

import lombok.Builder;
import lombok.Data;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ProviderAvailabilitySummaryDto {

    private UUID providerUuid;
    private String providerFullName;
    private String specialization;
    private List<DayScheduleDto> weeklySchedule;
    private SummaryDto summary;
    private List<BlockedSlotResponseDto> blockedSlots;

    @Data
    @Builder
    public static class DayScheduleDto {
        private DayOfWeek dayOfWeek;
        private List<AvailabilityResponseDto> slots;
    }

    @Data
    @Builder
    public static class SummaryDto {
        private Double totalHoursPerWeek;
        private Integer totalSlots;
        private Integer totalBlocks;
    }
}
