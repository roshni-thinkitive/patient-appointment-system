package com.appointment.exception;

public class PatientNotFoundException extends ResourceNotFoundException {

    public PatientNotFoundException(String message) {
        super(message);
    }

    public PatientNotFoundException(String fieldName, Object fieldValue) {
        super("Patient", fieldName, fieldValue);
    }
}
