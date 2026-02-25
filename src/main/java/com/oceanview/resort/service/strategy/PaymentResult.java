package com.oceanview.resort.service.strategy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents the result of executing a payment through a {@link PaymentStrategy}.
 *
 * <p><b>Design Pattern: Strategy Pattern (supporting class)</b></p>
 * <p>Each payment strategy returns a {@code PaymentResult} after processing,
 * providing a uniform response structure regardless of the underlying payment
 * method (cash, card, points, or online).</p>
 *
 * @see PaymentStrategy
 * @see PaymentRequest
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResult {

    /** Whether the payment was processed successfully. */
    private boolean success;

    /** The transaction reference generated during processing. */
    private String transactionReference;

    /** The amount that was actually charged. */
    private BigDecimal amountCharged;

    /** A human-readable message describing the outcome. */
    private String message;

    /** Timestamp of the payment processing. */
    private LocalDateTime processedAt;

    /**
     * Factory method to create a successful payment result.
     *
     * @param transactionRef the transaction reference
     * @param amount         the amount charged
     * @param message        a success message
     * @return a successful PaymentResult
     */
    public static PaymentResult success(String transactionRef, BigDecimal amount, String message) {
        return PaymentResult.builder()
                .success(true)
                .transactionReference(transactionRef)
                .amountCharged(amount)
                .message(message)
                .processedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Factory method to create a failed payment result.
     *
     * @param message the failure reason
     * @return a failed PaymentResult
     */
    public static PaymentResult failure(String message) {
        return PaymentResult.builder()
                .success(false)
                .message(message)
                .processedAt(LocalDateTime.now())
                .build();
    }
}
