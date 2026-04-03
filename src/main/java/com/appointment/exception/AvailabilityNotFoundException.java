package com.appointment.exception;

public class AvailabilityNotFoundException extends ResourceNotFoundException {

    public AvailabilityNotFoundException(String message) {
        super(message);
    }

    public AvailabilityNotFoundException(String fieldName, Object fieldValue) {
        super("Availability", fieldName, fieldValue);
    }
}
