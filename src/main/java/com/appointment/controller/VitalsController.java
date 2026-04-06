package com.appointment.controller;

import com.appointment.dto.common.ResponseHelper;
import com.appointment.dto.ehr.VitalsDto;
import com.appointment.service.VitalsService;
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
@RequestMapping("/api/ehr/v1/vitals")
@RequiredArgsConstructor
public class VitalsController {

    private final VitalsService vitalsService;

    @PostMapping("/{patientUuid}")
    public ResponseEntity<Map<String, Object>> createVitals(
            @PathVariable UUID patientUuid, @Valid @RequestBody VitalsDto dto) {
        VitalsDto result = vitalsService.createVitals(patientUuid, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseHelper.created("Vitals created successfully", result));
    }

    @PutMapping("/{patientUuid}/{uuid}")
    public ResponseEntity<Map<String, Object>> updateVitals(
            @PathVariable UUID patientUuid, @PathVariable UUID uuid,
            @Valid @RequestBody VitalsDto dto) {
        VitalsDto result = vitalsService.updateVitals(uuid, dto);
        return ResponseEntity.ok(ResponseHelper.success("Vitals updated successfully", result));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Map<String, Object>> getVitalsByUuid(@PathVariable UUID uuid) {
        VitalsDto result = vitalsService.getVitalsByUuid(uuid);
        return ResponseEntity.ok(ResponseHelper.success("Vitals fetched successfully", result));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Map<String, Object>> deleteVitals(@PathVariable UUID uuid) {
        vitalsService.deleteVitals(uuid);
        return ResponseEntity.ok(ResponseHelper.deleted("Vitals deleted successfully"));
    }

    @GetMapping("/{patientUuid}/all")
    public ResponseEntity<Map<String, Object>> getAllVitals(
            @PathVariable UUID patientUuid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        return ResponseEntity.ok(
                ResponseHelper.success("Vitals fetched successfully",
                        vitalsService.getAllVitals(patientUuid, pageable)));
    }
}
