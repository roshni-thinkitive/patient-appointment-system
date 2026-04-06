package com.appointment.serviceImpl;

import com.appointment.dto.appointment.AppointmentRequestDto;
import com.appointment.dto.appointment.AppointmentResponseDto;
import com.appointment.dto.appointment.AppointmentStatusUpdateDto;
import com.appointment.entity.Appointment;
import com.appointment.entity.BlockedSlot;
import com.appointment.entity.Demographics;
import com.appointment.entity.Patient;
import com.appointment.entity.Provider;
import com.appointment.enums.AppointmentStatus;
import com.appointment.exception.BadRequestException;
import com.appointment.exception.ResourceNotFoundException;
import com.appointment.repository.AppointmentRepository;
import com.appointment.repository.BlockedSlotRepository;
import com.appointment.repository.DemographicsRepository;
import com.appointment.repository.PatientRepository;
import com.appointment.repository.ProviderRepository;
import com.appointment.service.AppointmentService;
import com.appointment.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final ProviderRepository providerRepository;
    private final DemographicsRepository demographicsRepository;
    private final BlockedSlotRepository blockedSlotRepository;
    private final AvailabilityService availabilityService;

    @Override
    @Transactional
    public AppointmentResponseDto bookAppointment(AppointmentRequestDto dto) {
        Patient patient = patientRepository.findByUuid(dto.getPatientUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "uuid", dto.getPatientUuid()));

        Provider provider = providerRepository.findByUuid(dto.getProviderUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Provider", "uuid", dto.getProviderUuid()));

        LocalDate date = dto.getAppointmentDate();
        LocalTime startTime = dto.getStartTime();
        LocalTime endTime = dto.getEndTime();

        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new BadRequestException("Start time must be before end time");
        }

        boolean available = availabilityService.isProviderAvailable(
                provider.getUuid(), date, startTime, endTime);
        if (!available) {
            throw new BadRequestException("Provider is not available on this day/time");
        }

        boolean hasAppointmentOverlap = appointmentRepository
                .existsOverlappingAppointment(provider, date, startTime, endTime);
        if (hasAppointmentOverlap) {
            throw new BadRequestException(
                    "This slot is already booked: " + startTime + " to " + endTime);
        }

        boolean hasBlockedOverlap = blockedSlotRepository
                .existsOverlap(provider, date, startTime, endTime);
        if (hasBlockedOverlap) {
            throw new BadRequestException("Provider has blocked this time slot");
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .provider(provider)
                .appointmentDate(date)
                .startTime(startTime)
                .endTime(endTime)
                .status(AppointmentStatus.SCHEDULED)
                .notes(dto.getNotes())
                .build();

        appointment = appointmentRepository.save(appointment);

        BlockedSlot blockedSlot = BlockedSlot.builder()
                .provider(provider)
                .blockedDate(date)
                .blockStartTime(startTime)
                .blockEndTime(endTime)
                .reason("Appointment booked")
                .build();
        blockedSlotRepository.save(blockedSlot);

        return toAppointmentResponseDto(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponseDto getAppointmentByUuid(UUID uuid) {
        return toAppointmentResponseDto(findByUuid(uuid));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentResponseDto> getAppointmentsByPatient(UUID patientUuid, Pageable pageable) {
        Patient patient = patientRepository.findByUuid(patientUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "uuid", patientUuid));
        return appointmentRepository.findAllByPatient(patient, pageable)
                .map(this::toAppointmentResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentResponseDto> getAppointmentsByProvider(UUID providerUuid, Pageable pageable) {
        Provider provider = providerRepository.findByUuid(providerUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Provider", "uuid", providerUuid));
        return appointmentRepository.findAllByProvider(provider, pageable)
                .map(this::toAppointmentResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getAppointmentsByProviderAndDate(UUID providerUuid, LocalDate date) {
        Provider provider = providerRepository.findByUuid(providerUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Provider", "uuid", providerUuid));
        return appointmentRepository.findAllByProviderAndAppointmentDate(provider, date).stream()
                .map(this::toAppointmentResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AppointmentResponseDto updateStatus(UUID appointmentUuid, AppointmentStatusUpdateDto dto) {
        Appointment appointment = findByUuid(appointmentUuid);

        AppointmentStatus newStatus = dto.getStatus();
        appointment.setStatus(newStatus);

        if (newStatus == AppointmentStatus.CANCELLED) {
            appointment.setCancellationReason(dto.getCancellationReason());
            removeBlockedSlotForAppointment(appointment);
        }

        appointment = appointmentRepository.save(appointment);
        return toAppointmentResponseDto(appointment);
    }

    @Override
    @Transactional
    public void cancelAppointment(UUID appointmentUuid, String reason) {
        Appointment appointment = findByUuid(appointmentUuid);

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BadRequestException("Appointment is already cancelled");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(reason);
        removeBlockedSlotForAppointment(appointment);

        appointmentRepository.save(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentResponseDto> getAllAppointments(Pageable pageable) {
        return appointmentRepository.findAll(pageable)
                .map(this::toAppointmentResponseDto);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private Appointment findByUuid(UUID uuid) {
        return appointmentRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "uuid", uuid));
    }

    private void removeBlockedSlotForAppointment(Appointment appointment) {
        blockedSlotRepository.findByProviderAndBlockedDate(
                appointment.getProvider(), appointment.getAppointmentDate())
                .stream()
                .filter(slot ->
                        slot.getBlockStartTime().equals(appointment.getStartTime()) &&
                        slot.getBlockEndTime().equals(appointment.getEndTime()) &&
                        "Appointment booked".equals(slot.getReason()))
                .findFirst()
                .ifPresent(blockedSlotRepository::delete);
    }

    private AppointmentResponseDto toAppointmentResponseDto(Appointment a) {
        Patient patient = a.getPatient();
        Provider provider = a.getProvider();

        String patientFullName = demographicsRepository.findByPatient(patient)
                .map(d -> d.getFirstName() + " " + d.getLastName())
                .orElse("Unknown");

        String providerFullName = provider.getFirstName() + " " + provider.getLastName();

        return AppointmentResponseDto.builder()
                .uuid(a.getUuid())
                .patientUuid(patient.getUuid())
                .patientFullName(patientFullName)
                .providerUuid(provider.getUuid())
                .providerFullName(providerFullName)
                .specialization(provider.getSpecialization())
                .appointmentDate(a.getAppointmentDate())
                .dayOfWeek(a.getDayOfWeek())
                .startTime(a.getStartTime())
                .endTime(a.getEndTime())
                .status(a.getStatus())
                .notes(a.getNotes())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
