package com.appointment.controller;

import com.appointment.dto.availability.AvailabilityRequestDto;
import com.appointment.dto.availability.BlockedSlotRequestDto;
import com.appointment.dto.availability.BlockedSlotResponseDto;
import com.appointment.dto.availability.ProviderAvailabilitySummaryDto;
import com.appointment.dto.common.ResponseHelper;
import com.appointment.service.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @GetMapping("/{providerUuid}")
    public ResponseEntity<Map<String, Object>> getProviderFullAvailability(
            @PathVariable UUID providerUuid) {
        ProviderAvailabilitySummaryDto result = availabilityService.getProviderFullAvailability(providerUuid);
        return ResponseEntity.ok(
                ResponseHelper.success("Provider availability fetched successfully", result));
    }

    @PutMapping("/{providerUuid}")
    public ResponseEntity<Map<String, Object>> saveOrUpdateWeeklySchedule(
            @PathVariable UUID providerUuid,
            @Valid @RequestBody AvailabilityRequestDto dto) {
        ProviderAvailabilitySummaryDto result = availabilityService.saveOrUpdateWeeklySchedule(providerUuid, dto);
        return ResponseEntity.ok(
                ResponseHelper.success("Weekly schedule updated successfully", result));
    }

    @PostMapping("/{providerUuid}/block")
    public ResponseEntity<Map<String, Object>> addBlockTime(
            @PathVariable UUID providerUuid,
            @Valid @RequestBody BlockedSlotRequestDto dto) {
        BlockedSlotResponseDto result = availabilityService.addBlockedSlot(providerUuid, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseHelper.created("Block time added successfully", result));
    }

    @DeleteMapping("/{providerUuid}/block/{blockUuid}")
    public ResponseEntity<Map<String, Object>> removeBlockTime(
            @PathVariable UUID providerUuid,
            @PathVariable UUID blockUuid) {
        availabilityService.removeBlockedSlot(providerUuid, blockUuid);
        return ResponseEntity.ok(ResponseHelper.deleted("Block time removed successfully"));
    }
}
