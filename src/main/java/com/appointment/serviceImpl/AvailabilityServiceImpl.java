package com.appointment.serviceImpl;

import com.appointment.dto.availability.AvailabilityRequestDto;
import com.appointment.dto.availability.AvailabilityResponseDto;
import com.appointment.dto.availability.BlockedSlotRequestDto;
import com.appointment.dto.availability.BlockedSlotResponseDto;
import com.appointment.dto.availability.ProviderAvailabilitySummaryDto;
import com.appointment.entity.BlockedSlot;
import com.appointment.entity.Provider;
import com.appointment.entity.ProviderAvailability;
import com.appointment.exception.BadRequestException;
import com.appointment.exception.ResourceNotFoundException;
import com.appointment.repository.BlockedSlotRepository;
import com.appointment.repository.ProviderAvailabilityRepository;
import com.appointment.repository.ProviderRepository;
import com.appointment.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

    private final ProviderRepository providerRepository;
    private final ProviderAvailabilityRepository availabilityRepository;
    private final BlockedSlotRepository blockedSlotRepository;

    // ── Availability ──────────────────────────────────────────────────────────

    @Override
    @Transactional
    public AvailabilityResponseDto addAvailability(AvailabilityRequestDto dto) {
        Provider provider = findProviderByUuid(dto.getProviderUuid());

        if (dto.getStartTime().isAfter(dto.getEndTime()) || dto.getStartTime().equals(dto.getEndTime())) {
            throw new BadRequestException("Start time must be before end time");
        }

        ProviderAvailability availability = ProviderAvailability.builder()
                .provider(provider)
                .dayOfWeek(dto.getDayOfWeek())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();

        availability = availabilityRepository.save(availability);
        return toAvailabilityResponseDto(availability);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailabilityResponseDto> getProviderAvailability(UUID providerUuid) {
        Provider provider = findProviderByUuid(providerUuid);
        return availabilityRepository.findAllByProvider(provider).stream()
                .map(this::toAvailabilityResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProviderAvailabilitySummaryDto> getAllProvidersAvailability() {
        return providerRepository.findAll().stream()
                .map(provider -> ProviderAvailabilitySummaryDto.builder()
                        .providerUuid(provider.getUuid())
                        .providerFullName(provider.getFirstName() + " " + provider.getLastName())
                        .specialization(provider.getSpecialization())
                        .availability(availabilityRepository.findAllByProvider(provider).stream()
                                .map(this::toAvailabilityResponseDto)
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AvailabilityResponseDto updateAvailability(UUID availabilityUuid, AvailabilityRequestDto dto) {
        ProviderAvailability availability = availabilityRepository.findByUuid(availabilityUuid)
                .orElseThrow(() -> new ResourceNotFoundException("ProviderAvailability", "uuid", availabilityUuid));

        if (dto.getStartTime().isAfter(dto.getEndTime()) || dto.getStartTime().equals(dto.getEndTime())) {
            throw new BadRequestException("Start time must be before end time");
        }

        availability.setDayOfWeek(dto.getDayOfWeek());
        availability.setStartTime(dto.getStartTime());
        availability.setEndTime(dto.getEndTime());
        if (dto.getIsActive() != null) {
            availability.setIsActive(dto.getIsActive());
        }

        availability = availabilityRepository.save(availability);
        return toAvailabilityResponseDto(availability);
    }

    @Override
    @Transactional
    public void deleteAvailability(UUID availabilityUuid) {
        ProviderAvailability availability = availabilityRepository.findByUuid(availabilityUuid)
                .orElseThrow(() -> new ResourceNotFoundException("ProviderAvailability", "uuid", availabilityUuid));
        availabilityRepository.delete(availability);
    }

    // ── Blocked Slots ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public BlockedSlotResponseDto addBlockedSlot(BlockedSlotRequestDto dto) {
        Provider provider = findProviderByUuid(dto.getProviderUuid());

        if (dto.getBlockStartTime().isAfter(dto.getBlockEndTime())
                || dto.getBlockStartTime().equals(dto.getBlockEndTime())) {
            throw new BadRequestException("Block start time must be before block end time");
        }

        BlockedSlot blockedSlot = BlockedSlot.builder()
                .provider(provider)
                .blockedDate(dto.getBlockedDate())
                .blockStartTime(dto.getBlockStartTime())
                .blockEndTime(dto.getBlockEndTime())
                .reason(dto.getReason())
                .build();

        blockedSlot = blockedSlotRepository.save(blockedSlot);
        return toBlockedSlotResponseDto(blockedSlot);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlockedSlotResponseDto> getBlockedSlots(UUID providerUuid) {
        Provider provider = findProviderByUuid(providerUuid);
        return blockedSlotRepository.findAllByProvider(provider).stream()
                .map(this::toBlockedSlotResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeBlockedSlot(UUID blockedSlotUuid) {
        BlockedSlot slot = blockedSlotRepository.findByUuid(blockedSlotUuid)
                .orElseThrow(() -> new ResourceNotFoundException("BlockedSlot", "uuid", blockedSlotUuid));
        blockedSlotRepository.delete(slot);
    }

    // ── Availability Check ────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public boolean isProviderAvailable(UUID providerUuid, LocalDate date,
                                       LocalTime startTime, LocalTime endTime) {
        Provider provider = findProviderByUuid(providerUuid);

        log.info("Checking availability for provider: {}, date: {}, day: {}, start: {}, end: {}",
                providerUuid, date, date.getDayOfWeek(), startTime, endTime);

        // Check 1: Active availability record for this DayOfWeek
        ProviderAvailability availability = availabilityRepository
                .findByProviderAndDayOfWeek(provider, date.getDayOfWeek())
                .orElse(null);

        if (availability == null || !availability.getIsActive()) {
            log.info("No active availability found for day: {}", date.getDayOfWeek());
            throw new BadRequestException(
                "Provider has no availability set for " + date.getDayOfWeek()
                + " (" + date + ")");
        }

        log.info("Found availability: {} to {}, isActive: {}",
                availability.getStartTime(), availability.getEndTime(), availability.getIsActive());

        // Check 2: Requested time within available window
        boolean withinAvailableWindow = !startTime.isBefore(availability.getStartTime())
                && !endTime.isAfter(availability.getEndTime());

        if (!withinAvailableWindow) {
            log.info("Requested time {}–{} is outside available window {}–{}",
                    startTime, endTime, availability.getStartTime(), availability.getEndTime());
            throw new BadRequestException(
                "Provider is only available from " + availability.getStartTime()
                + " to " + availability.getEndTime()
                + " on " + date.getDayOfWeek()
                + ". Requested: " + startTime + " to " + endTime);
        }

        // Check 3: No blocked slot overlaps
        boolean hasOverlap = blockedSlotRepository.existsOverlap(provider, date, startTime, endTime);
        if (hasOverlap) {
            log.info("Blocked slot overlap found for date: {}, time: {}–{}", date, startTime, endTime);
            throw new BadRequestException(
                "Provider has blocked " + date
                + " from " + startTime + " to " + endTime
                + ". Slot is not available.");
        }

        log.info("Provider is available for date: {}, time: {}–{}", date, startTime, endTime);
        return true;
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private Provider findProviderByUuid(UUID uuid) {
        return providerRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Provider", "uuid", uuid));
    }

    private AvailabilityResponseDto toAvailabilityResponseDto(ProviderAvailability a) {
        return AvailabilityResponseDto.builder()
                .uuid(a.getUuid())
                .dayOfWeek(a.getDayOfWeek())
                .startTime(a.getStartTime())
                .endTime(a.getEndTime())
                .isActive(a.getIsActive())
                .build();
    }

    private BlockedSlotResponseDto toBlockedSlotResponseDto(BlockedSlot b) {
        return BlockedSlotResponseDto.builder()
                .uuid(b.getUuid())
                .blockedDate(b.getBlockedDate())
                .blockStartTime(b.getBlockStartTime())
                .blockEndTime(b.getBlockEndTime())
                .reason(b.getReason())
                .build();
    }
}