package com.appointment.serviceImpl;

import com.appointment.dto.ehr.AllergyDto;
import com.appointment.entity.Allergy;
import com.appointment.entity.Patient;
import com.appointment.enums.Severity;
import com.appointment.exception.ResourceNotFoundException;
import com.appointment.repository.AllergyRepository;
import com.appointment.repository.PatientRepository;
import com.appointment.service.AllergyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AllergyServiceImpl implements AllergyService {

    private final AllergyRepository allergyRepository;
    private final PatientRepository patientRepository;

    @Override
    @Transactional
    public AllergyDto createAllergy(UUID patientUuid, AllergyDto dto) {
        Patient patient = findPatientByUuid(patientUuid);
        Allergy allergy = Allergy.builder()
                .substance(dto.getSubstance())
                .reaction(dto.getReaction())
                .severity(dto.getSeverity())
                .onsetDate(dto.getOnsetDate())
                .patient(patient)
                .build();
        allergy = allergyRepository.save(allergy);
        return toAllergyDto(allergy);
    }

    @Override
    @Transactional
    public AllergyDto updateAllergy(UUID uuid, AllergyDto dto) {
        Allergy allergy = allergyRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Allergy", "uuid", uuid));
        allergy.setSubstance(dto.getSubstance());
        allergy.setReaction(dto.getReaction());
        allergy.setSeverity(dto.getSeverity());
        allergy.setOnsetDate(dto.getOnsetDate());
        allergy = allergyRepository.save(allergy);
        return toAllergyDto(allergy);
    }

    @Override
    @Transactional(readOnly = true)
    public AllergyDto getAllergyByUuid(UUID uuid) {
        Allergy allergy = allergyRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Allergy", "uuid", uuid));
        return toAllergyDto(allergy);
    }

    @Override
    @Transactional
    public void deleteAllergy(UUID uuid) {
        Allergy allergy = allergyRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Allergy", "uuid", uuid));
        allergy.setIsDeleted(true);
        allergyRepository.save(allergy);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AllergyDto> getAllAllergies(UUID patientUuid, Severity severity, String searchString, Pageable pageable) {
        Patient patient = patientUuid != null ? findPatientByUuid(patientUuid) : null;
        return allergyRepository.findAllFiltered(patient, severity, searchString, pageable)
                .map(this::toAllergyDto);
    }

    private Patient findPatientByUuid(UUID uuid) {
        return patientRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "uuid", uuid));
    }

    private AllergyDto toAllergyDto(Allergy a) {
        return AllergyDto.builder()
                .uuid(a.getUuid())
                .substance(a.getSubstance())
                .reaction(a.getReaction())
                .severity(a.getSeverity())
                .onsetDate(a.getOnsetDate())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
