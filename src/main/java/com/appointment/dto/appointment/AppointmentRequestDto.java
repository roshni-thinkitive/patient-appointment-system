package com.appointment.dto.appointment;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class AppointmentRequestDto {

    @NotNull(message = "Patient UUID is required")
    private UUID patientUuid;

    @NotNull(message = "Provider UUID is required")
    private UUID providerUuid;

    @NotNull(message = "Appointment date is required")
    private LocalDate appointmentDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    private String notes;
}
