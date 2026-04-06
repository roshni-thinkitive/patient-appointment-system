package com.appointment.dto.ehr;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class VitalsDto {

    private UUID uuid;

    private Double bpSystolic;

    private Double bpDiastolic;

    private Double heartRate;

    private Double temperature;

    private Double weight;

    private Double height;

    private Double respiratoryRate;

    private Double o2Saturation;

    private Double bmi;

    private LocalDateTime createdAt;
}
