package com.oceanview.resort.model.enums;

/**
 * Enumeration representing the lifecycle statuses of a reservation.
 * Typical flow: PENDING → CONFIRMED → CHECKED_IN → CHECKED_OUT
 */
public enum ReservationStatus {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    CHECKED_IN("Checked In"),
    CHECKED_OUT("Checked Out"),
    CANCELLED("Cancelled");

    private final String displayName;

    ReservationStatus(String displayName) { this.displayName = displayName; }

    public String getDisplayName() { return displayName; }
}

