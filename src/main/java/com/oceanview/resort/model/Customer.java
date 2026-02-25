package com.oceanview.resort.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Customer entity extending {@link User}.
 *
 * <p>Represents a hotel guest with additional attributes such as address and
 * loyalty points. Maps to the {@code customers} table via JPA
 * {@link InheritanceType#JOINED} strategy defined on the parent
 * {@link User} entity.</p>
 *
 * <h3>Relationships</h3>
 * <ul>
 *   <li>{@code reservations} – one-to-many relationship with {@link Reservation}
 *       (mapped by {@code Reservation.customer}). Lazy-loaded and excluded from
 *       {@code toString()} / {@code equals()} / {@code hashCode()} to prevent
 *       infinite recursion.</li>
 * </ul>
 *
 * @see User
 * @see Reservation
 */
@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"reservations"})
@ToString(callSuper = true, exclude = {"reservations"})
public class Customer extends User {

    /** Guest's postal / residential address. */
    @Column(columnDefinition = "TEXT")
    private String address;

    /** Accumulated loyalty points earned through stays. */
    @Column(name = "loyalty_points")
    @Min(value = 0, message = "Loyalty points cannot be negative")
    private int loyaltyPoints = 0;

    /**
     * Reservations made by this customer.
     * Lazy-loaded; changes cascade through the {@link Reservation} side.
     */
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();

    /**
     * Adds loyalty points earned from a completed stay.
     *
     * @param points the number of points to add (must be positive)
     * @throws IllegalArgumentException if {@code points} is negative
     */
    public void addLoyaltyPoints(int points) {
        if (points < 0) {
            throw new IllegalArgumentException("Points to add must not be negative");
        }
        this.loyaltyPoints += points;
    }

    /**
     * Redeems (deducts) loyalty points when used as payment.
     *
     * @param points the number of points to redeem (must be positive)
     * @throws IllegalArgumentException if {@code points} is negative or exceeds current balance
     */
    public void redeemLoyaltyPoints(int points) {
        if (points < 0) {
            throw new IllegalArgumentException("Points to redeem must not be negative");
        }
        if (points > this.loyaltyPoints) {
            throw new IllegalArgumentException("Insufficient loyalty points");
        }
        this.loyaltyPoints -= points;
    }
}
