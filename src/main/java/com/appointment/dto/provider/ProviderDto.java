package com.appointment.dto.provider;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProviderDto {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String specialization;

    private String licenseNumber;

    private String phoneNumber;
}
