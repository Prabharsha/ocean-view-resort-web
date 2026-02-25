package com.oceanview.resort.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a loyalty points transaction at Ocean View Resort.
 *
 * <p>Records every earning and redemption of loyalty points for audit and
 * history purposes. Each transaction links to a {@link Customer} and stores
 * the points change, type (EARN or REDEEM), and a description.</p>
 */
@Entity
@Table(name = "loyalty_transactions", indexes = {
        @Index(name = "idx_loyalty_customer", columnList = "customer_id"),
        @Index(name = "idx_loyalty_timestamp", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyTransaction {

    /** UUID primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** The customer this transaction belongs to. */
    @Column(name = "customer_id", nullable = false, length = 36)
    private String customerId;

    /** Number of points involved (positive for EARN, negative for REDEEM). */
    @Column(nullable = false)
    private int points;

    /** Type of transaction: EARN or REDEEM. */
    @Column(name = "transaction_type", nullable = false, length = 10)
    private String transactionType;

    /** Description of the transaction (e.g. "Earned from reservation OVR-2026-000001"). */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Reference ID (e.g. reservation ID, bill ID). */
    @Column(name = "reference_id", length = 36)
    private String referenceId;

    /** Timestamp of the transaction. */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
