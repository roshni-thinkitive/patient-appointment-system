package com.appointment.exception;

public class InvalidAppointmentTimeException extends BadRequestException {

    public InvalidAppointmentTimeException(String message) {
        super(message);
    }
}
