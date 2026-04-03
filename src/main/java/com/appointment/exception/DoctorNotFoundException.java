package com.appointment.exception;

public class DoctorNotFoundException extends ResourceNotFoundException {

    public DoctorNotFoundException(String message) {
        super(message);
    }

    public DoctorNotFoundException(String fieldName, Object fieldValue) {
        super("Doctor", fieldName, fieldValue);
    }
}
