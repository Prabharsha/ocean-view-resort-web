package com.oceanview.resort.model.enums;

/**
 * Enumeration representing the lifecycle statuses of a reservation.
 * Defines the valid state transitions for a booking at Ocean View Resort.
 *
 * <p>Typical flow: {@code PENDING → CONFIRMED → CHECKED_IN → CHECKED_OUT}</p>
 * <p>A reservation may be {@code CANCELLED} from {@code PENDING} or {@code CONFIRMED} states.</p>
 */
public enum ReservationStatus {

    /** Reservation has been created but not yet confirmed by staff. */
    PENDING("Pending"),

    /** Reservation has been confirmed and the room is reserved. */
    CONFIRMED("Confirmed"),

    /** Guest has arrived and checked into the room. */
    CHECKED_IN("Checked In"),

    /** Guest has completed their stay and checked out. */
    CHECKED_OUT("Checked Out"),

    /** Reservation has been cancelled before check-in. */
    CANCELLED("Cancelled");

    private final String displayName;

    ReservationStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable display name for this status.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
}
