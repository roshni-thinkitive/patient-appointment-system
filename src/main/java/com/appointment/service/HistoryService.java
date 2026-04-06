package com.appointment.service;

import com.appointment.dto.ehr.HistoryDto;
import com.appointment.enums.HistoryType;
import com.appointment.enums.Relationship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface HistoryService {

    HistoryDto createHistory(UUID patientUuid, HistoryDto dto);

    HistoryDto updateHistory(UUID uuid, HistoryDto dto);

    HistoryDto getHistoryByUuid(UUID uuid);

    void deleteHistory(UUID uuid);

    Page<HistoryDto> getAllHistories(UUID patientUuid, HistoryType historyType, Relationship relation,
                                     String searchString, Pageable pageable);
}
