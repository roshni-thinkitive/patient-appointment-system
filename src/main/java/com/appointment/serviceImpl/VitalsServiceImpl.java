package com.appointment.serviceImpl;

import com.appointment.dto.ehr.VitalsDto;
import com.appointment.entity.Patient;
import com.appointment.entity.Vitals;
import com.appointment.exception.ResourceNotFoundException;
import com.appointment.repository.PatientRepository;
import com.appointment.repository.VitalsRepository;
import com.appointment.service.VitalsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VitalsServiceImpl implements VitalsService {

    private final VitalsRepository vitalsRepository;
    private final PatientRepository patientRepository;

    @Override
    @Transactional
    public VitalsDto createVitals(UUID patientUuid, VitalsDto dto) {
        Patient patient = findPatientByUuid(patientUuid);
        Vitals vitals = Vitals.builder()
                .bpSystolic(dto.getBpSystolic())
                .bpDiastolic(dto.getBpDiastolic())
                .heartRate(dto.getHeartRate())
                .temperature(dto.getTemperature())
                .weight(dto.getWeight())
                .height(dto.getHeight())
                .respiratoryRate(dto.getRespiratoryRate())
                .o2Saturation(dto.getO2Saturation())
                .bmi(calculateBmi(dto.getWeight(), dto.getHeight()))
                .patient(patient)
                .build();
        vitals = vitalsRepository.save(vitals);
        return toVitalsDto(vitals);
    }

    @Override
    @Transactional
    public VitalsDto updateVitals(UUID uuid, VitalsDto dto) {
        Vitals vitals = vitalsRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Vitals", "uuid", uuid));
        vitals.setBpSystolic(dto.getBpSystolic());
        vitals.setBpDiastolic(dto.getBpDiastolic());
        vitals.setHeartRate(dto.getHeartRate());
        vitals.setTemperature(dto.getTemperature());
        vitals.setWeight(dto.getWeight());
        vitals.setHeight(dto.getHeight());
        vitals.setRespiratoryRate(dto.getRespiratoryRate());
        vitals.setO2Saturation(dto.getO2Saturation());
        vitals.setBmi(calculateBmi(dto.getWeight(), dto.getHeight()));
        vitals = vitalsRepository.save(vitals);
        return toVitalsDto(vitals);
    }

    @Override
    @Transactional(readOnly = true)
    public VitalsDto getVitalsByUuid(UUID uuid) {
        Vitals vitals = vitalsRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Vitals", "uuid", uuid));
        return toVitalsDto(vitals);
    }

    @Override
    @Transactional
    public void deleteVitals(UUID uuid) {
        Vitals vitals = vitalsRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Vitals", "uuid", uuid));
        vitals.setIsDeleted(true);
        vitalsRepository.save(vitals);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VitalsDto> getAllVitals(UUID patientUuid, Pageable pageable) {
        Patient patient = patientUuid != null ? findPatientByUuid(patientUuid) : null;
        return vitalsRepository.findAllFiltered(patient, pageable)
                .map(this::toVitalsDto);
    }

    private Double calculateBmi(Double weight, Double height) {
        if (weight == null || height == null || height == 0) {
            return null;
        }
        return (weight * 703) / (height * height);
    }

    private Patient findPatientByUuid(UUID uuid) {
        return patientRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "uuid", uuid));
    }

    private VitalsDto toVitalsDto(Vitals v) {
        return VitalsDto.builder()
                .uuid(v.getUuid())
                .bpSystolic(v.getBpSystolic())
                .bpDiastolic(v.getBpDiastolic())
                .heartRate(v.getHeartRate())
                .temperature(v.getTemperature())
                .weight(v.getWeight())
                .height(v.getHeight())
                .respiratoryRate(v.getRespiratoryRate())
                .o2Saturation(v.getO2Saturation())
                .bmi(v.getBmi())
                .createdAt(v.getCreatedAt())
                .build();
    }
}
