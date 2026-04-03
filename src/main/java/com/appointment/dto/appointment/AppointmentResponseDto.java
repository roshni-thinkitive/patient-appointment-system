package com.appointment.dto.appointment;

import com.appointment.enums.AppointmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
public class AppointmentResponseDto {

    private UUID uuid;

    private UUID patientUuid;
    private String patientFullName;

    private UUID providerUuid;
    private String providerFullName;
    private String specialization;

    private LocalDate appointmentDate;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    private AppointmentStatus status;
    private String notes;

    private LocalDateTime createdAt;
}
