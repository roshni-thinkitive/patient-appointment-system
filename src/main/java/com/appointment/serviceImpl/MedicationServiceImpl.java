package com.appointment.serviceImpl;

import com.appointment.dto.ehr.MedicationDto;
import com.appointment.entity.Medication;
import com.appointment.entity.Patient;
import com.appointment.exception.ResourceNotFoundException;
import com.appointment.repository.MedicationRepository;
import com.appointment.repository.PatientRepository;
import com.appointment.service.MedicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MedicationServiceImpl implements MedicationService {

    private final MedicationRepository medicationRepository;
    private final PatientRepository patientRepository;

    @Override
    @Transactional
    public MedicationDto createMedication(UUID patientUuid, MedicationDto dto) {
        Patient patient = findPatientByUuid(patientUuid);
        Medication medication = Medication.builder()
                .name(dto.getName())
                .dosage(dto.getDosage())
                .frequency(dto.getFrequency())
                .route(dto.getRoute())
                .prescriber(dto.getPrescriber())
                .startDate(dto.getStartDate())
                .patient(patient)
                .build();
        medication = medicationRepository.save(medication);
        return toMedicationDto(medication);
    }

    @Override
    @Transactional
    public MedicationDto updateMedication(UUID uuid, MedicationDto dto) {
        Medication medication = medicationRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Medication", "uuid", uuid));
        medication.setName(dto.getName());
        medication.setDosage(dto.getDosage());
        medication.setFrequency(dto.getFrequency());
        medication.setRoute(dto.getRoute());
        medication.setPrescriber(dto.getPrescriber());
        medication.setStartDate(dto.getStartDate());
        medication = medicationRepository.save(medication);
        return toMedicationDto(medication);
    }

    @Override
    @Transactional(readOnly = true)
    public MedicationDto getMedicationByUuid(UUID uuid) {
        Medication medication = medicationRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Medication", "uuid", uuid));
        return toMedicationDto(medication);
    }

    @Override
    @Transactional
    public void deleteMedication(UUID uuid) {
        Medication medication = medicationRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Medication", "uuid", uuid));
        medication.setIsDeleted(true);
        medicationRepository.save(medication);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedicationDto> getAllMedications(UUID patientUuid, String searchString, Pageable pageable) {
        Patient patient = patientUuid != null ? findPatientByUuid(patientUuid) : null;
        return medicationRepository.findAllFiltered(patient, searchString, pageable)
                .map(this::toMedicationDto);
    }

    private Patient findPatientByUuid(UUID uuid) {
        return patientRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "uuid", uuid));
    }

    private MedicationDto toMedicationDto(Medication m) {
        return MedicationDto.builder()
                .uuid(m.getUuid())
                .name(m.getName())
                .dosage(m.getDosage())
                .frequency(m.getFrequency())
                .route(m.getRoute())
                .prescriber(m.getPrescriber())
                .startDate(m.getStartDate())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
