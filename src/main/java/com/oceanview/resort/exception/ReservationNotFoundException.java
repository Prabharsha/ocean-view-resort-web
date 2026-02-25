package com.oceanview.resort.exception;

/**
 * Exception thrown when a reservation cannot be found in the system.
 */
public class ReservationNotFoundException extends RuntimeException {

    public ReservationNotFoundException(String message) {
        super(message);
    }

    public ReservationNotFoundException(String field, String value) {
        super(String.format("Reservation with %s '%s' not found", field, value));
    }
}
