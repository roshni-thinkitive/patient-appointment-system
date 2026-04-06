package com.appointment.controller;

import com.appointment.dto.appointment.AppointmentRequestDto;
import com.appointment.dto.appointment.AppointmentResponseDto;
import com.appointment.dto.appointment.AppointmentStatusUpdateDto;
import com.appointment.dto.common.ResponseHelper;
import com.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/book")
    public ResponseEntity<Map<String, Object>> bookAppointment(
            @Valid @RequestBody AppointmentRequestDto dto) {
        AppointmentResponseDto result = appointmentService.bookAppointment(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseHelper.created("Appointment booked successfully", result));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Map<String, Object>> getAppointmentByUuid(@PathVariable UUID uuid) {
        AppointmentResponseDto result = appointmentService.getAppointmentByUuid(uuid);
        return ResponseEntity.ok(ResponseHelper.success("Appointment fetched successfully", result));
    }

    @GetMapping("/patient/{patientUuid}")
    public ResponseEntity<Map<String, Object>> getAppointmentsByPatient(
            @PathVariable UUID patientUuid,
            @PageableDefault(page = 0, size = 10, sort = "appointmentDate",
                    direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(
                ResponseHelper.success("Patient appointments fetched successfully",
                        appointmentService.getAppointmentsByPatient(patientUuid, pageable)));
    }

    @GetMapping("/provider/{providerUuid}")
    public ResponseEntity<Map<String, Object>> getAppointmentsByProvider(
            @PathVariable UUID providerUuid,
            @PageableDefault(page = 0, size = 10, sort = "appointmentDate",
                    direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(
                ResponseHelper.success("Provider appointments fetched successfully",
                        appointmentService.getAppointmentsByProvider(providerUuid, pageable)));
    }

    @GetMapping("/provider/{providerUuid}/date")
    public ResponseEntity<Map<String, Object>> getAppointmentsByProviderAndDate(
            @PathVariable UUID providerUuid,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(
                ResponseHelper.success("Appointments fetched successfully",
                        appointmentService.getAppointmentsByProviderAndDate(providerUuid, date)));
    }

    @PutMapping("/{uuid}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable UUID uuid,
            @Valid @RequestBody AppointmentStatusUpdateDto dto) {
        AppointmentResponseDto result = appointmentService.updateStatus(uuid, dto);
        return ResponseEntity.ok(ResponseHelper.success("Appointment status updated successfully", result));
    }

    @DeleteMapping("/{uuid}/cancel")
    public ResponseEntity<Map<String, Object>> cancelAppointment(
            @PathVariable UUID uuid,
            @RequestParam(required = false) String reason) {
        appointmentService.cancelAppointment(uuid, reason);
        return ResponseEntity.ok(ResponseHelper.deleted("Appointment cancelled successfully"));
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllAppointments(
            @PageableDefault(page = 0, size = 10, sort = "appointmentDate",
                    direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(
                ResponseHelper.success("All appointments fetched successfully",
                        appointmentService.getAllAppointments(pageable)));
    }
}
