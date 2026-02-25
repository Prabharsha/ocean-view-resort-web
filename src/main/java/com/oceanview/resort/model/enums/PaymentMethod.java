package com.oceanview.resort.model.enums;

/**
 * Enumeration of supported payment methods at Ocean View Resort.
 * Each method represents a distinct payment channel available to guests.
 *
 * <ul>
 *   <li>{@link #CASH}   – Physical cash payment at the front desk.</li>
 *   <li>{@link #CARD}   – Credit or debit card payment.</li>
 *   <li>{@link #POINTS} – Payment using accumulated loyalty points.</li>
 *   <li>{@link #ONLINE} – Online payment via payment gateway.</li>
 * </ul>
 */
public enum PaymentMethod {

    /** Physical cash payment at the front desk. */
    CASH("Cash"),

    /** Credit or debit card payment. */
    CARD("Card"),

    /** Payment using accumulated loyalty points. */
    POINTS("Loyalty Points"),

    /** Online payment via payment gateway. */
    ONLINE("Online");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable display name for this payment method.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
}
