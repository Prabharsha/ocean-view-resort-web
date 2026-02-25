package com.oceanview.resort.model.enums;

/**
 * Enumeration of available room types at Ocean View Resort.
 * Each type represents a different tier of accommodation with varying amenities and pricing.
 *
 * <ul>
 *   <li>{@link #STANDARD}  – Basic room with essential amenities.</li>
 *   <li>{@link #DELUXE}    – Upgraded room with premium furnishings.</li>
 *   <li>{@link #SUITE}     – Spacious suite with separate living area.</li>
 *   <li>{@link #PENTHOUSE} – Top-tier accommodation with panoramic views.</li>
 * </ul>
 */
public enum RoomType {

    /** Basic room with essential amenities. */
    STANDARD("Standard Room"),

    /** Upgraded room with premium furnishings and additional amenities. */
    DELUXE("Deluxe Room"),

    /** Spacious suite with separate living area and luxury amenities. */
    SUITE("Suite"),

    /** Top-tier accommodation with panoramic views and exclusive services. */
    PENTHOUSE("Penthouse");

    private final String displayName;

    RoomType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable display name for this room type.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
}
