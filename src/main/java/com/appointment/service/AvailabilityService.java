package com.appointment.service;

import com.appointment.dto.availability.AvailabilityRequestDto;
import com.appointment.dto.availability.AvailabilityResponseDto;
import com.appointment.dto.availability.BlockedSlotRequestDto;
import com.appointment.dto.availability.BlockedSlotResponseDto;
import com.appointment.dto.availability.ProviderAvailabilitySummaryDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface AvailabilityService {

    AvailabilityResponseDto addAvailability(AvailabilityRequestDto dto);

    List<AvailabilityResponseDto> getProviderAvailability(UUID providerUuid);

    List<ProviderAvailabilitySummaryDto> getAllProvidersAvailability();

    AvailabilityResponseDto updateAvailability(UUID availabilityUuid, AvailabilityRequestDto dto);

    void deleteAvailability(UUID availabilityUuid);

    BlockedSlotResponseDto addBlockedSlot(BlockedSlotRequestDto dto);

    List<BlockedSlotResponseDto> getBlockedSlots(UUID providerUuid);

    void removeBlockedSlot(UUID blockedSlotUuid);

    boolean isProviderAvailable(UUID providerUuid, LocalDate date, LocalTime startTime, LocalTime endTime);
}
