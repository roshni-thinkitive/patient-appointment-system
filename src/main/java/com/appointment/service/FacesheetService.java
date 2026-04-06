package com.appointment.service;

import com.appointment.dto.ehr.FacesheetSummaryDto;

import java.util.UUID;

public interface FacesheetService {

    FacesheetSummaryDto getFacesheet(UUID patientUuid);
}
