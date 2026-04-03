package com.appointment.controller;

import com.appointment.dto.common.ResponseHelper;
import com.appointment.dto.patient.*;
import com.appointment.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPatient(
            @Valid @RequestBody PatientRegistrationDto dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        PatientResponseDto result = patientService.createPatient(dto, email);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseHelper.created("Patient created successfully", result));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Map<String, Object>> getPatientByUuid(@PathVariable UUID uuid) {
        PatientResponseDto result = patientService.getPatientByUuid(uuid);
        return ResponseEntity.ok(ResponseHelper.success("Patient fetched successfully", result));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPatients(
            @PageableDefault(page = 0, size = 10, sort = "createdAt",
                    direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(
                ResponseHelper.success("Patients fetched successfully",
                        patientService.getAllPatients(pageable)));
    }

    @PutMapping("/{uuid}/demographics")
    public ResponseEntity<Map<String, Object>> updateDemographics(
            @PathVariable UUID uuid,
            @Valid @RequestBody DemographicsDto dto) {
        PatientResponseDto result = patientService.updateDemographics(uuid, dto);
        return ResponseEntity.ok(ResponseHelper.success("Demographics updated successfully", result));
    }

    @PutMapping("/{uuid}/contact")
    public ResponseEntity<Map<String, Object>> updateContactInfo(
            @PathVariable UUID uuid,
            @Valid @RequestBody ContactInfoDto dto) {
        PatientResponseDto result = patientService.updateContactInfo(uuid, dto);
        return ResponseEntity.ok(ResponseHelper.success("Contact info updated successfully", result));
    }

    @PostMapping("/{uuid}/emergency-contact")
    public ResponseEntity<Map<String, Object>> addEmergencyContact(
            @PathVariable UUID uuid,
            @Valid @RequestBody EmergencyContactDto dto) {
        EmergencyContactDto result = patientService.addEmergencyContact(uuid, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseHelper.created("Emergency contact added successfully", result));
    }

    @PostMapping("/{uuid}/insurance")
    public ResponseEntity<Map<String, Object>> addInsurance(
            @PathVariable UUID uuid,
            @Valid @RequestBody InsuranceDto dto) {
        InsuranceDto result = patientService.addInsurance(uuid, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseHelper.created("Insurance added successfully", result));
    }
}
