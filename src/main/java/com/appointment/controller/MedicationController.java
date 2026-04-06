package com.appointment.controller;

import com.appointment.dto.common.ResponseHelper;
import com.appointment.dto.ehr.MedicationDto;
import com.appointment.service.MedicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/ehr/v1/medications")
@RequiredArgsConstructor
public class MedicationController {

    private final MedicationService medicationService;

    @PostMapping("/{patientUuid}")
    public ResponseEntity<Map<String, Object>> createMedication(
            @PathVariable UUID patientUuid, @Valid @RequestBody MedicationDto dto) {
        MedicationDto result = medicationService.createMedication(patientUuid, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseHelper.created("Medication created successfully", result));
    }

    @PutMapping("/{patientUuid}/{uuid}")
    public ResponseEntity<Map<String, Object>> updateMedication(
            @PathVariable UUID patientUuid, @PathVariable UUID uuid,
            @Valid @RequestBody MedicationDto dto) {
        MedicationDto result = medicationService.updateMedication(uuid, dto);
        return ResponseEntity.ok(ResponseHelper.success("Medication updated successfully", result));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Map<String, Object>> getMedicationByUuid(@PathVariable UUID uuid) {
        MedicationDto result = medicationService.getMedicationByUuid(uuid);
        return ResponseEntity.ok(ResponseHelper.success("Medication fetched successfully", result));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Map<String, Object>> deleteMedication(@PathVariable UUID uuid) {
        medicationService.deleteMedication(uuid);
        return ResponseEntity.ok(ResponseHelper.deleted("Medication deleted successfully"));
    }

    @GetMapping("/{patientUuid}/all")
    public ResponseEntity<Map<String, Object>> getAllMedications(
            @PathVariable UUID patientUuid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(required = false) String searchString) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        return ResponseEntity.ok(
                ResponseHelper.success("Medications fetched successfully",
                        medicationService.getAllMedications(patientUuid, searchString, pageable)));
    }
}
