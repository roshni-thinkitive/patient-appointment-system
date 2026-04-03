package com.appointment.dto.appointment;

import com.appointment.enums.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppointmentStatusUpdateDto {

    @NotNull(message = "Status is required")
    private AppointmentStatus status;

    private String cancellationReason;
}
