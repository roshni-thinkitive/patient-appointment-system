package com.appointment.dto.patient;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class ContactInfoDto {

    private String phoneNumber;

    private String alternatePhone;

    @Email(message = "Email must be valid")
    private String email;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String country;

    private String zipCode;
}
