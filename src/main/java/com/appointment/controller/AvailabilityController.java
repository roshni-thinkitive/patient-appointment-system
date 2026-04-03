package com.appointment.controller;

import com.appointment.dto.availability.AvailabilityRequestDto;
import com.appointment.dto.availability.AvailabilityResponseDto;
import com.appointment.dto.availability.BlockedSlotRequestDto;
import com.appointment.dto.availability.BlockedSlotResponseDto;
import com.appointment.dto.availability.ProviderAvailabilitySummaryDto;
import com.appointment.dto.common.ResponseHelper;
import com.appointment.service.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    // ── Availability CRUD ─────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<Map<String, Object>> addAvailability(
            @Valid @RequestBody AvailabilityRequestDto dto) {
        AvailabilityResponseDto result = availabilityService.addAvailability(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseHelper.created("Availability added successfully", result));
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllProvidersAvailability() {
        return ResponseEntity.ok(
                ResponseHelper.success("All providers availability fetched successfully",
                        availabilityService.getAllProvidersAvailability()));
    }

    @GetMapping("/provider/{providerUuid}")
    public ResponseEntity<Map<String, Object>> getProviderAvailability(
            @PathVariable UUID providerUuid) {
        return ResponseEntity.ok(
                ResponseHelper.success("Availability fetched successfully",
                        availabilityService.getProviderAvailability(providerUuid)));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<Map<String, Object>> updateAvailability(
            @PathVariable UUID uuid,
            @Valid @RequestBody AvailabilityRequestDto dto) {
        AvailabilityResponseDto result = availabilityService.updateAvailability(uuid, dto);
        return ResponseEntity.ok(ResponseHelper.success("Availability updated successfully", result));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Map<String, Object>> deleteAvailability(@PathVariable UUID uuid) {
        availabilityService.deleteAvailability(uuid);
        return ResponseEntity.ok(ResponseHelper.deleted("Availability deleted successfully"));
    }

    // ── Blocked Slots ─────────────────────────────────────────────────────────

    @PostMapping("/block")
    public ResponseEntity<Map<String, Object>> addBlockedSlot(
            @Valid @RequestBody BlockedSlotRequestDto dto) {
        BlockedSlotResponseDto result = availabilityService.addBlockedSlot(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseHelper.created("Slot blocked successfully", result));
    }

    @GetMapping("/block/{providerUuid}")
    public ResponseEntity<Map<String, Object>> getBlockedSlots(
            @PathVariable UUID providerUuid) {
        return ResponseEntity.ok(
                ResponseHelper.success("Blocked slots fetched successfully",
                        availabilityService.getBlockedSlots(providerUuid)));
    }

    @DeleteMapping("/block/{uuid}")
    public ResponseEntity<Map<String, Object>> removeBlockedSlot(@PathVariable UUID uuid) {
        availabilityService.removeBlockedSlot(uuid);
        return ResponseEntity.ok(ResponseHelper.deleted("Blocked slot removed successfully"));
    }

    // ── Availability Check ────────────────────────────────────────────────────

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> isProviderAvailable(
            @RequestParam UUID providerUuid,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {
        boolean available = availabilityService.isProviderAvailable(providerUuid, date, startTime, endTime);
        return ResponseEntity.ok(ResponseHelper.success("Availability checked", available));
    }
}
