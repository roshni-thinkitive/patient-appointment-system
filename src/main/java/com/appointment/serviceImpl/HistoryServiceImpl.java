package com.appointment.serviceImpl;

import com.appointment.dto.ehr.HistoryDto;
import com.appointment.entity.History;
import com.appointment.entity.Patient;
import com.appointment.enums.HistoryType;
import com.appointment.enums.Relationship;
import com.appointment.exception.ResourceNotFoundException;
import com.appointment.repository.HistoryRepository;
import com.appointment.repository.PatientRepository;
import com.appointment.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;
    private final PatientRepository patientRepository;

    @Override
    @Transactional
    public HistoryDto createHistory(UUID patientUuid, HistoryDto dto) {
        Patient patient = findPatientByUuid(patientUuid);
        History history = History.builder()
                .name(dto.getName())
                .date(dto.getDate())
                .note(dto.getNote())
                .recordedDate(dto.getRecordedDate())
                .onSetAge(dto.getOnSetAge())
                .historyType(dto.getHistoryType())
                .relation(dto.getRelation())
                .condition(dto.getCondition())
                .notes(dto.getNotes())
                .patient(patient)
                .build();
        history = historyRepository.save(history);
        return toHistoryDto(history);
    }

    @Override
    @Transactional
    public HistoryDto updateHistory(UUID uuid, HistoryDto dto) {
        History history = historyRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("History", "uuid", uuid));
        history.setName(dto.getName());
        history.setDate(dto.getDate());
        history.setNote(dto.getNote());
        history.setRecordedDate(dto.getRecordedDate());
        history.setOnSetAge(dto.getOnSetAge());
        history.setHistoryType(dto.getHistoryType());
        history.setRelation(dto.getRelation());
        history.setCondition(dto.getCondition());
        history.setNotes(dto.getNotes());
        history = historyRepository.save(history);
        return toHistoryDto(history);
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryDto getHistoryByUuid(UUID uuid) {
        History history = historyRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("History", "uuid", uuid));
        return toHistoryDto(history);
    }

    @Override
    @Transactional
    public void deleteHistory(UUID uuid) {
        History history = historyRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("History", "uuid", uuid));
        history.setIsDeleted(true);
        historyRepository.save(history);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HistoryDto> getAllHistories(UUID patientUuid, HistoryType historyType, Relationship relation,
                                            String searchString, Pageable pageable) {
        Patient patient = patientUuid != null ? findPatientByUuid(patientUuid) : null;
        return historyRepository.findAllFiltered(patient, historyType, relation, searchString, pageable)
                .map(this::toHistoryDto);
    }

    private Patient findPatientByUuid(UUID uuid) {
        return patientRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "uuid", uuid));
    }

    private HistoryDto toHistoryDto(History h) {
        return HistoryDto.builder()
                .uuid(h.getUuid())
                .name(h.getName())
                .date(h.getDate())
                .note(h.getNote())
                .recordedDate(h.getRecordedDate())
                .onSetAge(h.getOnSetAge())
                .historyType(h.getHistoryType())
                .relation(h.getRelation())
                .condition(h.getCondition())
                .notes(h.getNotes())
                .createdAt(h.getCreatedAt())
                .build();
    }
}
