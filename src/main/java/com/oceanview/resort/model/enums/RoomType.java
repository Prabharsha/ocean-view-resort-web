package com.oceanview.resort.model.enums;

/**
 * Enumeration of available room types at Ocean View Resort.
 */
public enum RoomType {
    STANDARD("Standard Room"),
    DELUXE("Deluxe Room"),
    SUITE("Suite"),
    PENTHOUSE("Penthouse");

    private final String displayName;

    RoomType(String displayName) { this.displayName = displayName; }

    public String getDisplayName() { return displayName; }
}

