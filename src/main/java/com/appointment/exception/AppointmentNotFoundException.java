package com.appointment.exception;

public class AppointmentNotFoundException extends ResourceNotFoundException {

    public AppointmentNotFoundException(String message) {
        super(message);
    }

    public AppointmentNotFoundException(String fieldName, Object fieldValue) {
        super("Appointment", fieldName, fieldValue);
    }
}
