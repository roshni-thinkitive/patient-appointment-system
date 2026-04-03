package com.appointment.dto.provider;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ProviderResponseDto {

    private UUID uuid;
    private String createdBy;
    private String firstName;
    private String lastName;
    private String specialization;
    private String licenseNumber;
    private String phoneNumber;
    private LocalDateTime createdAt;
}
