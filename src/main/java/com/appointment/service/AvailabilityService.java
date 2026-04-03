package com.appointment.service;

import com.appointment.dto.availability.AvailabilityRequestDto;
import com.appointment.dto.availability.BlockedSlotRequestDto;
import com.appointment.dto.availability.BlockedSlotResponseDto;
import com.appointment.dto.availability.ProviderAvailabilitySummaryDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public interface AvailabilityService {

    ProviderAvailabilitySummaryDto getProviderFullAvailability(UUID providerUuid);

    ProviderAvailabilitySummaryDto saveOrUpdateWeeklySchedule(UUID providerUuid, AvailabilityRequestDto dto);

    BlockedSlotResponseDto addBlockedSlot(UUID providerUuid, BlockedSlotRequestDto dto);

    void removeBlockedSlot(UUID providerUuid, UUID blockedSlotUuid);

    boolean isProviderAvailable(UUID providerUuid, LocalDate date, LocalTime startTime, LocalTime endTime);
}
