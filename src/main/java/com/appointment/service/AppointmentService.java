package com.appointment.service;

import com.appointment.dto.appointment.AppointmentRequestDto;
import com.appointment.dto.appointment.AppointmentResponseDto;
import com.appointment.dto.appointment.AppointmentStatusUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AppointmentService {

    AppointmentResponseDto bookAppointment(AppointmentRequestDto dto);

    AppointmentResponseDto getAppointmentByUuid(UUID uuid);

    Page<AppointmentResponseDto> getAppointmentsByPatient(UUID patientUuid, Pageable pageable);

    Page<AppointmentResponseDto> getAppointmentsByProvider(UUID providerUuid, Pageable pageable);

    List<AppointmentResponseDto> getAppointmentsByProviderAndDate(UUID providerUuid, LocalDate date);

    AppointmentResponseDto updateStatus(UUID appointmentUuid, AppointmentStatusUpdateDto dto);

    void cancelAppointment(UUID appointmentUuid, String reason);

    Page<AppointmentResponseDto> getAllAppointments(Pageable pageable);
}
