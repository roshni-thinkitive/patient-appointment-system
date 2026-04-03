package com.appointment.dto.availability;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ProviderAvailabilitySummaryDto {

    private UUID providerUuid;
    private String providerFullName;
    private String specialization;
    private List<AvailabilityResponseDto> availability;
}
