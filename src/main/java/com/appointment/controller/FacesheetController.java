package com.appointment.controller;

import com.appointment.dto.common.ResponseHelper;
import com.appointment.dto.ehr.FacesheetSummaryDto;
import com.appointment.service.FacesheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/ehr/v1/facesheet")
@RequiredArgsConstructor
public class FacesheetController {

    private final FacesheetService facesheetService;

    @GetMapping("/{patientUuid}")
    public ResponseEntity<Map<String, Object>> getFacesheet(@PathVariable UUID patientUuid) {
        FacesheetSummaryDto result = facesheetService.getFacesheet(patientUuid);
        return ResponseEntity.ok(ResponseHelper.success("Facesheet fetched successfully", result));
    }
}
