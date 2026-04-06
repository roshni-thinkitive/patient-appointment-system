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

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

    private final ProviderRepository providerRepository;
    private final ProviderAvailabilityRepository availabilityRepository;
    private final BlockedSlotRepository blockedSlotRepository;

    private static final DayOfWeek[] DAYS_ORDER = {
            DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY
    };

    @Override
    @Transactional(readOnly = true)
    public ProviderAvailabilitySummaryDto getProviderFullAvailability(UUID providerUuid) {
        Provider provider = findProviderByUuid(providerUuid);

        List<ProviderAvailability> allSlots = availabilityRepository.findAllByProvider(provider);
        List<BlockedSlot> allBlocks = blockedSlotRepository.findAllByProvider(provider);

        // Group slots by day of week
        Map<DayOfWeek, List<ProviderAvailability>> slotsByDay = allSlots.stream()
                .collect(Collectors.groupingBy(ProviderAvailability::getDayOfWeek));

        // Build weekly schedule in Sun → Sat order
        List<ProviderAvailabilitySummaryDto.DayScheduleDto> weeklySchedule = new ArrayList<>();
        for (DayOfWeek day : DAYS_ORDER) {
            List<ProviderAvailability> daySlots = slotsByDay.getOrDefault(day, List.of());
            List<AvailabilityResponseDto> slotDtos = daySlots.stream()
                    .map(this::toSlotResponseDto)
                    .collect(Collectors.toList());

            weeklySchedule.add(ProviderAvailabilitySummaryDto.DayScheduleDto.builder()
                    .dayOfWeek(day)
                    .slots(slotDtos)
                    .build());
        }

        // Calculate summary
        double totalHours = allSlots.stream()
                .mapToDouble(s -> Duration.between(s.getStartTime(), s.getEndTime()).toMinutes() / 60.0)
                .sum();
        int totalSlots = allSlots.size();
        int totalBlocks = allBlocks.size();

        ProviderAvailabilitySummaryDto.SummaryDto summary = ProviderAvailabilitySummaryDto.SummaryDto.builder()
                .totalHoursPerWeek(totalHours)
                .totalSlots(totalSlots)
                .totalBlocks(totalBlocks)
                .build();

        // Build blocked slots response
        List<BlockedSlotResponseDto> blockedSlots = allBlocks.stream()
                .map(this::toBlockedSlotResponseDto)
                .collect(Collectors.toList());

        return ProviderAvailabilitySummaryDto.builder()
                .providerUuid(provider.getUuid())
                .providerFullName(provider.getFirstName() + " " + provider.getLastName())
                .specialization(provider.getSpecialization())
                .weeklySchedule(weeklySchedule)
                .summary(summary)
                .blockedSlots(blockedSlots)
                .build();
    }

    @Override
    @Transactional
    public ProviderAvailabilitySummaryDto saveOrUpdateWeeklySchedule(UUID providerUuid, AvailabilityRequestDto dto) {
        Provider provider = findProviderByUuid(providerUuid);

        // Validate all slots
        for (AvailabilityRequestDto.DaySchedule day : dto.getWeeklySchedule()) {
            for (AvailabilityRequestDto.Slot slot : day.getSlots()) {
                if (slot.getStartTime().isAfter(slot.getEndTime()) || slot.getStartTime().equals(slot.getEndTime())) {
                    throw new BadRequestException(
                            "Start time must be before end time for " + day.getDayOfWeek()
                            + " slot: " + slot.getStartTime() + " - " + slot.getEndTime());
                }
            }
        }

        // Delete all existing slots for this provider, then insert new ones
        availabilityRepository.deleteAllByProvider(provider);
        availabilityRepository.flush();

        for (AvailabilityRequestDto.DaySchedule day : dto.getWeeklySchedule()) {
            for (AvailabilityRequestDto.Slot slot : day.getSlots()) {
                ProviderAvailability entity = ProviderAvailability.builder()
                        .provider(provider)
                        .dayOfWeek(day.getDayOfWeek())
                        .startTime(slot.getStartTime())
                        .endTime(slot.getEndTime())
                        .isActive(true)
                        .timezone(slot.getTimezone())
                        .location(slot.getLocation())
                        .build();
                availabilityRepository.save(entity);
            }
        }

        return getProviderFullAvailability(providerUuid);
    }

    @Override
    @Transactional
    public BlockedSlotResponseDto addBlockedSlot(UUID providerUuid, BlockedSlotRequestDto dto) {
        Provider provider = findProviderByUuid(providerUuid);

        LocalTime startTime;
        LocalTime endTime;

        if (Boolean.TRUE.equals(dto.getBlockEntireDay())) {
            startTime = LocalTime.of(0, 0);
            endTime = LocalTime.of(23, 59);
        } else {
            if (dto.getStartTime() == null || dto.getEndTime() == null) {
                throw new BadRequestException("Start time and end time are required when blockEntireDay is false");
            }
            if (dto.getStartTime().isAfter(dto.getEndTime()) || dto.getStartTime().equals(dto.getEndTime())) {
                throw new BadRequestException("Block start time must be before block end time");
            }
            startTime = dto.getStartTime();
            endTime = dto.getEndTime();
        }

        BlockedSlot blockedSlot = BlockedSlot.builder()
                .provider(provider)
                .blockType(dto.getBlockType())
                .blockedDate(dto.getDate())
                .blockStartTime(startTime)
                .blockEndTime(endTime)
                .blockEntireDay(Boolean.TRUE.equals(dto.getBlockEntireDay()))
                .notes(dto.getNotes())
                .build();

        blockedSlot = blockedSlotRepository.save(blockedSlot);
        return toBlockedSlotResponseDto(blockedSlot);
    }

    @Override
    @Transactional
    public void removeBlockedSlot(UUID providerUuid, UUID blockedSlotUuid) {
        Provider provider = findProviderByUuid(providerUuid);
        BlockedSlot slot = blockedSlotRepository.findByUuid(blockedSlotUuid)
                .orElseThrow(() -> new ResourceNotFoundException("BlockedSlot", "uuid", blockedSlotUuid));

        if (!slot.getProvider().getId().equals(provider.getId())) {
            throw new ResourceNotFoundException("BlockedSlot", "uuid", blockedSlotUuid);
        }

        blockedSlotRepository.delete(slot);
    }

    @Override
    @Transactional
    public AvailabilityResponseDto updateSlot(UUID providerUuid, UUID slotUuid, AvailabilityRequestDto.Slot dto) {
        Provider provider = findProviderByUuid(providerUuid);
        ProviderAvailability slot = availabilityRepository.findByUuid(slotUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", "uuid", slotUuid));

        if (!slot.getProvider().getId().equals(provider.getId())) {
            throw new ResourceNotFoundException("Slot", "uuid", slotUuid);
        }

        if (dto.getStartTime().isAfter(dto.getEndTime()) || dto.getStartTime().equals(dto.getEndTime())) {
            throw new BadRequestException("Start time must be before end time");
        }

        slot.setStartTime(dto.getStartTime());
        slot.setEndTime(dto.getEndTime());
        slot.setTimezone(dto.getTimezone());
        slot.setLocation(dto.getLocation());

        slot = availabilityRepository.save(slot);
        return toSlotResponseDto(slot);
    }

    @Override
    @Transactional
    public void deleteSlot(UUID providerUuid, UUID slotUuid) {
        Provider provider = findProviderByUuid(providerUuid);
        ProviderAvailability slot = availabilityRepository.findByUuid(slotUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", "uuid", slotUuid));

        if (!slot.getProvider().getId().equals(provider.getId())) {
            throw new ResourceNotFoundException("Slot", "uuid", slotUuid);
        }

        availabilityRepository.delete(slot);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProviderAvailable(UUID providerUuid, LocalDate date,
                                       LocalTime startTime, LocalTime endTime) {
        Provider provider = findProviderByUuid(providerUuid);

        log.info("Checking availability for provider: {}, date: {}, day: {}, start: {}, end: {}",
                providerUuid, date, date.getDayOfWeek(), startTime, endTime);

        // Check 1: Find all active slots for this day of week
        List<ProviderAvailability> daySlots = availabilityRepository
                .findAllByProviderAndDayOfWeek(provider, date.getDayOfWeek())
                .stream()
                .filter(ProviderAvailability::getIsActive)
                .collect(Collectors.toList());

        if (daySlots.isEmpty()) {
            log.info("No active availability found for day: {}", date.getDayOfWeek());
            throw new BadRequestException(
                    "Provider has no availability set for " + date.getDayOfWeek()
                    + " (" + date + ")");
        }

        // Check 2: Requested time must fit within at least one slot
        boolean fitsInAnySlot = daySlots.stream().anyMatch(slot ->
                !startTime.isBefore(slot.getStartTime()) && !endTime.isAfter(slot.getEndTime()));

        if (!fitsInAnySlot) {
            String availableWindows = daySlots.stream()
                    .map(s -> s.getStartTime() + " - " + s.getEndTime())
                    .collect(Collectors.joining(", "));
            log.info("Requested time {}–{} does not fit any slot on {}: [{}]",
                    startTime, endTime, date.getDayOfWeek(), availableWindows);
            throw new BadRequestException(
                    "Provider is available during [" + availableWindows
                    + "] on " + date.getDayOfWeek()
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

    private AvailabilityResponseDto toSlotResponseDto(ProviderAvailability a) {
        return AvailabilityResponseDto.builder()
                .slotUuid(a.getUuid())
                .startTime(a.getStartTime())
                .endTime(a.getEndTime())
                .timezone(a.getTimezone())
                .location(a.getLocation())
                .build();
    }

    private BlockedSlotResponseDto toBlockedSlotResponseDto(BlockedSlot b) {
        return BlockedSlotResponseDto.builder()
                .blockUuid(b.getUuid())
                .blockType(b.getBlockType())
                .date(b.getBlockedDate())
                .startTime(b.getBlockStartTime())
                .endTime(b.getBlockEndTime())
                .blockEntireDay(b.getBlockEntireDay())
                .notes(b.getNotes())
                .build();
    }
}
