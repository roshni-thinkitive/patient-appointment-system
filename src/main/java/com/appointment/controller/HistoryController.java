package com.appointment.controller;

import com.appointment.dto.common.ResponseHelper;
import com.appointment.dto.ehr.HistoryDto;
import com.appointment.enums.HistoryType;
import com.appointment.enums.Relationship;
import com.appointment.service.HistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/ehr/v1/histories")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @PostMapping("/{patientUuid}")
    public ResponseEntity<Map<String, Object>> createHistory(
            @PathVariable UUID patientUuid, @Valid @RequestBody HistoryDto dto) {
        HistoryDto result = historyService.createHistory(patientUuid, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseHelper.created("History created successfully", result));
    }

    @PutMapping("/{patientUuid}/{uuid}")
    public ResponseEntity<Map<String, Object>> updateHistory(
            @PathVariable UUID patientUuid, @PathVariable UUID uuid,
            @Valid @RequestBody HistoryDto dto) {
        HistoryDto result = historyService.updateHistory(uuid, dto);
        return ResponseEntity.ok(ResponseHelper.success("History updated successfully", result));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Map<String, Object>> getHistoryByUuid(@PathVariable UUID uuid) {
        HistoryDto result = historyService.getHistoryByUuid(uuid);
        return ResponseEntity.ok(ResponseHelper.success("History fetched successfully", result));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Map<String, Object>> deleteHistory(@PathVariable UUID uuid) {
        historyService.deleteHistory(uuid);
        return ResponseEntity.ok(ResponseHelper.deleted("History deleted successfully"));
    }

    @GetMapping("/{patientUuid}/all")
    public ResponseEntity<Map<String, Object>> getAllHistories(
            @PathVariable UUID patientUuid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(required = false) String searchString,
            @RequestParam(required = false) HistoryType history,
            @RequestParam(required = false) Relationship relative) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        return ResponseEntity.ok(
                ResponseHelper.success("Histories fetched successfully",
                        historyService.getAllHistories(patientUuid, history, relative, searchString, pageable)));
    }
}
