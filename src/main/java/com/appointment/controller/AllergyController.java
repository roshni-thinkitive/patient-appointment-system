package com.appointment.controller;

import com.appointment.dto.common.ResponseHelper;
import com.appointment.dto.ehr.AllergyDto;
import com.appointment.enums.Severity;
import com.appointment.service.AllergyService;
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
@RequestMapping("/api/ehr/v1/allergies")
@RequiredArgsConstructor
public class AllergyController {

    private final AllergyService allergyService;

    @PostMapping("/{patientUuid}")
    public ResponseEntity<Map<String, Object>> createAllergy(
            @PathVariable UUID patientUuid, @Valid @RequestBody AllergyDto dto) {
        AllergyDto result = allergyService.createAllergy(patientUuid, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseHelper.created("Allergy created successfully", result));
    }

    @PutMapping("/{patientUuid}/{uuid}")
    public ResponseEntity<Map<String, Object>> updateAllergy(
            @PathVariable UUID patientUuid, @PathVariable UUID uuid,
            @Valid @RequestBody AllergyDto dto) {
        AllergyDto result = allergyService.updateAllergy(uuid, dto);
        return ResponseEntity.ok(ResponseHelper.success("Allergy updated successfully", result));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Map<String, Object>> getAllergyByUuid(@PathVariable UUID uuid) {
        AllergyDto result = allergyService.getAllergyByUuid(uuid);
        return ResponseEntity.ok(ResponseHelper.success("Allergy fetched successfully", result));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Map<String, Object>> deleteAllergy(@PathVariable UUID uuid) {
        allergyService.deleteAllergy(uuid);
        return ResponseEntity.ok(ResponseHelper.deleted("Allergy deleted successfully"));
    }

    @GetMapping("/{patientUuid}/all")
    public ResponseEntity<Map<String, Object>> getAllAllergies(
            @PathVariable UUID patientUuid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(required = false) String searchString,
            @RequestParam(required = false) Severity severity) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        return ResponseEntity.ok(
                ResponseHelper.success("Allergies fetched successfully",
                        allergyService.getAllAllergies(patientUuid, severity, searchString, pageable)));
    }
}
