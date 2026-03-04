package com.oceanview.resort.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Customer model extending User with additional guest-specific fields.
 * Maps to the 'customers' table (joined with 'users').
 */
public class Customer extends User {

    private String address;
    private int loyaltyPoints = 0;
    private List<Reservation> reservations = new ArrayList<>();

    public Customer() { super(); }

    /** Adds loyalty points earned from a completed stay. */
    public void addLoyaltyPoints(int points) {
        if (points < 0) throw new IllegalArgumentException("Points to add must not be negative");
        this.loyaltyPoints += points;
    }

    /** Redeems (deducts) loyalty points when used as payment. */
    public void redeemLoyaltyPoints(int points) {
        if (points < 0) throw new IllegalArgumentException("Points to redeem must not be negative");
        if (points > this.loyaltyPoints) throw new IllegalArgumentException("Insufficient loyalty points");
        this.loyaltyPoints -= points;
    }

    // ── Getters & Setters ──
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }

    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }
}

