package com.oceanview.resort.model.enums;

/**
 * Enumeration of supported payment methods at Ocean View Resort.
 */
public enum PaymentMethod {
    CASH("Cash"),
    CARD("Card"),
    POINTS("Loyalty Points"),
    ONLINE("Online");

    private final String displayName;

    PaymentMethod(String displayName) { this.displayName = displayName; }

    public String getDisplayName() { return displayName; }
}

