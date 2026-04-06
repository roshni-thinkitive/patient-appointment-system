package com.appointment.serviceImpl;

import com.appointment.dto.patient.*;
import com.appointment.entity.*;
import com.appointment.exception.ResourceNotFoundException;
import com.appointment.repository.*;
import com.appointment.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final DemographicsRepository demographicsRepository;
    private final ContactInfoRepository contactInfoRepository;
    private final EmergencyContactRepository emergencyContactRepository;
    private final InsuranceRepository insuranceRepository;

    @Override
    @Transactional
    public PatientResponseDto createPatient(PatientRegistrationDto dto, String email) {
        // Create patient directly — not linked to a User account.
        // createdBy stores the email of the logged-in provider/admin who created this record.
        Patient patient = Patient.builder()
                .createdBy(email)
                .build();
        patient = patientRepository.save(patient);

        // Save Demographics
        DemographicsDto demDto = dto.getDemographics();
        Demographics demographics = Demographics.builder()
                .patient(patient)
                .firstName(demDto.getFirstName())
                .lastName(demDto.getLastName())
                .dateOfBirth(demDto.getDateOfBirth())
                .gender(demDto.getGender())
                .bloodGroup(demDto.getBloodGroup())
                .maritalStatus(demDto.getMaritalStatus())
                .nationality(demDto.getNationality())
                .preferredLanguage(demDto.getPreferredLanguage())
                .build();
        demographicsRepository.save(demographics);

        // Save ContactInfo (optional)
        ContactInfo contactInfo = null;
        if (dto.getContactInfo() != null) {
            ContactInfoDto ciDto = dto.getContactInfo();
            contactInfo = ContactInfo.builder()
                    .patient(patient)
                    .phoneNumber(ciDto.getPhoneNumber())
                    .alternatePhone(ciDto.getAlternatePhone())
                    .email(ciDto.getEmail())
                    .addressLine1(ciDto.getAddressLine1())
                    .addressLine2(ciDto.getAddressLine2())
                    .city(ciDto.getCity())
                    .state(ciDto.getState())
                    .country(ciDto.getCountry())
                    .zipCode(ciDto.getZipCode())
                    .build();
            contactInfoRepository.save(contactInfo);
        }

        // Save EmergencyContact (optional — singular)
        if (dto.getEmergencyContact() != null) {
            EmergencyContactDto ecDto = dto.getEmergencyContact();
            EmergencyContact ec = new EmergencyContact();
            ec.setPatient(patient);
            ec.setContactName(ecDto.getContactName());
            ec.setRelationship(ecDto.getRelationship());
            ec.setPhoneNumber(ecDto.getPhoneNumber());
            ec.setAlternatePhone(ecDto.getAlternatePhone());
            ec.setEmail(ecDto.getEmail());
            emergencyContactRepository.save(ec);
        }

        // Save Insurance (optional — singular)
        if (dto.getInsurance() != null) {
            InsuranceDto insDto = dto.getInsurance();
            Insurance ins = new Insurance();
            ins.setPatient(patient);
            ins.setInsuranceName(insDto.getInsuranceName());
            ins.setPayerId(insDto.getPayerId());
            ins.setMemberId(insDto.getMemberId());
            ins.setGroupNumber(insDto.getGroupNumber());
            ins.setPolicyHolderName(insDto.getPolicyHolderName());
            ins.setRelationship(insDto.getRelationship());
            ins.setStartDate(insDto.getStartDate());
            ins.setEndDate(insDto.getEndDate());
            ins.setIsPrimary(insDto.getIsPrimary());
            insuranceRepository.save(ins);
        }

        return buildPatientResponseFromDb(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponseDto getPatientByUuid(UUID uuid) {
        return buildPatientResponseFromDb(findPatientByUuid(uuid));
    }

    @Override
    @Transactional
    public PatientResponseDto updateDemographics(UUID uuid, DemographicsDto dto) {
        Patient patient = findPatientByUuid(uuid);

        Demographics demographics = demographicsRepository.findByPatient(patient)
                .orElseGet(() -> Demographics.builder().patient(patient).build());

        demographics.setFirstName(dto.getFirstName());
        demographics.setLastName(dto.getLastName());
        demographics.setDateOfBirth(dto.getDateOfBirth());
        demographics.setGender(dto.getGender());
        demographics.setBloodGroup(dto.getBloodGroup());
        demographics.setMaritalStatus(dto.getMaritalStatus());
        demographics.setNationality(dto.getNationality());
        demographics.setPreferredLanguage(dto.getPreferredLanguage());

        demographicsRepository.save(demographics);
        return buildPatientResponseFromDb(patient);
    }

    @Override
    @Transactional
    public PatientResponseDto updateContactInfo(UUID uuid, ContactInfoDto dto) {
        Patient patient = findPatientByUuid(uuid);

        ContactInfo contactInfo = contactInfoRepository.findByPatient(patient)
                .orElseGet(() -> ContactInfo.builder().patient(patient).build());

        contactInfo.setPhoneNumber(dto.getPhoneNumber());
        contactInfo.setAlternatePhone(dto.getAlternatePhone());
        contactInfo.setEmail(dto.getEmail());
        contactInfo.setAddressLine1(dto.getAddressLine1());
        contactInfo.setAddressLine2(dto.getAddressLine2());
        contactInfo.setCity(dto.getCity());
        contactInfo.setState(dto.getState());
        contactInfo.setCountry(dto.getCountry());
        contactInfo.setZipCode(dto.getZipCode());

        contactInfoRepository.save(contactInfo);
        return buildPatientResponseFromDb(patient);
    }

    @Override
    @Transactional
    public EmergencyContactDto addEmergencyContact(UUID uuid, EmergencyContactDto dto) {
        Patient patient = findPatientByUuid(uuid);

        EmergencyContact ec = EmergencyContact.builder()
                .patient(patient)
                .contactName(dto.getContactName())
                .relationship(dto.getRelationship())
                .phoneNumber(dto.getPhoneNumber())
                .alternatePhone(dto.getAlternatePhone())
                .email(dto.getEmail())
                .build();

        ec = emergencyContactRepository.save(ec);
        return toEmergencyContactDto(ec);
    }

    @Override
    @Transactional
    public InsuranceDto addInsurance(UUID uuid, InsuranceDto dto) {
        Patient patient = findPatientByUuid(uuid);

        Insurance insurance = Insurance.builder()
                .patient(patient)
                .insuranceName(dto.getInsuranceName())
                .payerId(dto.getPayerId())
                .memberId(dto.getMemberId())
                .groupNumber(dto.getGroupNumber())
                .policyHolderName(dto.getPolicyHolderName())
                .relationship(dto.getRelationship())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .isPrimary(dto.getIsPrimary())
                .build();

        insurance = insuranceRepository.save(insurance);
        return toInsuranceDto(insurance);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PatientResponseDto> getAllPatients(Pageable pageable) {
        return patientRepository.findAllByIsDeletedFalse(pageable)
                .map(this::buildPatientResponseFromDb);
    }

    @Override
    @Transactional
    public void deletePatient(UUID uuid) {
        Patient patient = findPatientByUuid(uuid);
        patient.setIsDeleted(true);
        patientRepository.save(patient);
    }

    @Override
    @Transactional
    public PatientResponseDto updateAllPatientFields(UUID uuid, PatientRegistrationDto dto) {
        Patient patient = findPatientByUuid(uuid);

        // Update demographics
        DemographicsDto demDto = dto.getDemographics();
        Demographics demographics = demographicsRepository.findByPatient(patient)
                .orElseGet(() -> Demographics.builder().patient(patient).build());
        demographics.setFirstName(demDto.getFirstName());
        demographics.setLastName(demDto.getLastName());
        demographics.setDateOfBirth(demDto.getDateOfBirth());
        demographics.setGender(demDto.getGender());
        demographics.setBloodGroup(demDto.getBloodGroup());
        demographics.setMaritalStatus(demDto.getMaritalStatus());
        demographics.setNationality(demDto.getNationality());
        demographics.setPreferredLanguage(demDto.getPreferredLanguage());
        demographicsRepository.save(demographics);

        // Update contact info
        if (dto.getContactInfo() != null) {
            ContactInfoDto ciDto = dto.getContactInfo();
            ContactInfo contactInfo = contactInfoRepository.findByPatient(patient)
                    .orElseGet(() -> ContactInfo.builder().patient(patient).build());
            contactInfo.setPhoneNumber(ciDto.getPhoneNumber());
            contactInfo.setAlternatePhone(ciDto.getAlternatePhone());
            contactInfo.setEmail(ciDto.getEmail());
            contactInfo.setAddressLine1(ciDto.getAddressLine1());
            contactInfo.setAddressLine2(ciDto.getAddressLine2());
            contactInfo.setCity(ciDto.getCity());
            contactInfo.setState(ciDto.getState());
            contactInfo.setCountry(ciDto.getCountry());
            contactInfo.setZipCode(ciDto.getZipCode());
            contactInfoRepository.save(contactInfo);
        }

        return buildPatientResponseFromDb(patient);
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private Patient findPatientByUuid(UUID uuid) {
        return patientRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "uuid", uuid));
    }

    private PatientResponseDto buildPatientResponseFromDb(Patient patient) {
        Demographics demographics = demographicsRepository.findByPatient(patient).orElse(null);
        ContactInfo contactInfo = contactInfoRepository.findByPatient(patient).orElse(null);
        List<EmergencyContact> emergencyContacts = emergencyContactRepository.findAllByPatient(patient);
        List<Insurance> insurances = insuranceRepository.findAllByPatient(patient);

        return PatientResponseDto.builder()
                .uuid(patient.getUuid())
                .createdBy(patient.getCreatedBy())
                .demographics(demographics != null ? toDemographicsDto(demographics) : null)
                .contactInfo(contactInfo != null ? toContactInfoDto(contactInfo) : null)
                .emergencyContacts(emergencyContacts.stream()
                        .map(this::toEmergencyContactDto)
                        .collect(Collectors.toList()))
                .insurances(insurances.stream()
                        .map(this::toInsuranceDto)
                        .collect(Collectors.toList()))
                .createdAt(patient.getCreatedAt())
                .build();
    }

    private DemographicsDto toDemographicsDto(Demographics d) {
        DemographicsDto dto = new DemographicsDto();
        dto.setFirstName(d.getFirstName());
        dto.setLastName(d.getLastName());
        dto.setDateOfBirth(d.getDateOfBirth());
        dto.setGender(d.getGender());
        dto.setBloodGroup(d.getBloodGroup());
        dto.setMaritalStatus(d.getMaritalStatus());
        dto.setNationality(d.getNationality());
        dto.setPreferredLanguage(d.getPreferredLanguage());
        return dto;
    }

    private ContactInfoDto toContactInfoDto(ContactInfo c) {
        ContactInfoDto dto = new ContactInfoDto();
        dto.setPhoneNumber(c.getPhoneNumber());
        dto.setAlternatePhone(c.getAlternatePhone());
        dto.setEmail(c.getEmail());
        dto.setAddressLine1(c.getAddressLine1());
        dto.setAddressLine2(c.getAddressLine2());
        dto.setCity(c.getCity());
        dto.setState(c.getState());
        dto.setCountry(c.getCountry());
        dto.setZipCode(c.getZipCode());
        return dto;
    }

    private EmergencyContactDto toEmergencyContactDto(EmergencyContact ec) {
        EmergencyContactDto dto = new EmergencyContactDto();
        dto.setUuid(ec.getUuid());
        dto.setContactName(ec.getContactName());
        dto.setRelationship(ec.getRelationship());
        dto.setPhoneNumber(ec.getPhoneNumber());
        dto.setAlternatePhone(ec.getAlternatePhone());
        dto.setEmail(ec.getEmail());
        return dto;
    }

    private InsuranceDto toInsuranceDto(Insurance ins) {
        InsuranceDto dto = new InsuranceDto();
        dto.setUuid(ins.getUuid());
        dto.setInsuranceName(ins.getInsuranceName());
        dto.setPayerId(ins.getPayerId());
        dto.setMemberId(ins.getMemberId());
        dto.setGroupNumber(ins.getGroupNumber());
        dto.setPolicyHolderName(ins.getPolicyHolderName());
        dto.setRelationship(ins.getRelationship());
        dto.setStartDate(ins.getStartDate());
        dto.setEndDate(ins.getEndDate());
        dto.setIsPrimary(ins.getIsPrimary());
        return dto;
    }
}
