package com.oceanview.resort.service.strategy;

import com.oceanview.resort.model.enums.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Concrete Strategy for processing <b>loyalty points</b> payments.
 *
 * <p><b>Design Pattern: Strategy Pattern – Concrete Strategy</b></p>
 * <p>Implements the {@link PaymentStrategy} interface for loyalty-points-based
 * transactions. Points are converted to a monetary value at a fixed rate
 * (1 point = £0.10). In a production system this would verify the customer's
 * points balance from a loyalty database.</p>
 *
 * <h3>Processing Logic:</h3>
 * <ol>
 *   <li>Validates the payment amount is positive.</li>
 *   <li>Calculates the number of points required (amount ÷ conversion rate).</li>
 *   <li>Simulates points deduction from the customer's loyalty account.</li>
 *   <li>Returns a successful result with a points redemption reference.</li>
 * </ol>
 *
 * <h3>Conversion Rate:</h3>
 * <ul>
 *   <li>1 loyalty point = £0.10</li>
 *   <li>£100 payment requires 1,000 points</li>
 * </ul>
 *
 * @see PaymentStrategy
 * @see com.oceanview.resort.service.factory.PaymentStrategyFactory
 */
@Component
@Slf4j
public class PointsPaymentStrategy implements PaymentStrategy {

    /** Conversion rate: 1 loyalty point = £0.10 */
    private static final BigDecimal POINTS_CONVERSION_RATE = new BigDecimal("0.10");

    /**
     * Processes a payment using loyalty points by calculating and deducting
     * the required number of points from the customer's account.
     *
     * @param request the payment request details
     * @return {@link PaymentResult} with success status and points redemption reference
     */
    @Override
    public PaymentResult execute(PaymentRequest request) {
        log.info("Processing POINTS payment of {} for bill {}",
                request.getAmount(), request.getBillId());

        // Validate amount
        if (request.getAmount() == null || request.getAmount().signum() <= 0) {
            return PaymentResult.failure("Points payment amount must be positive");
        }

        // Calculate points required
        long pointsRequired = request.getAmount()
                .divide(POINTS_CONVERSION_RATE, 0, java.math.RoundingMode.CEILING)
                .longValue();

        log.info("Points required: {} (at rate {} per point)", pointsRequired, POINTS_CONVERSION_RATE);

        // Simulate points balance check and deduction
        // In production, this would call a loyalty service to verify and deduct points
        String redemptionRef = "PTS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        log.info("Points payment processed successfully. Redemption ref: {}. Points deducted: {}",
                redemptionRef, pointsRequired);

        return PaymentResult.success(
                redemptionRef,
                request.getAmount(),
                "Loyalty points payment processed. " + pointsRequired
                        + " points redeemed. Reference: " + redemptionRef
        );
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link PaymentMethod#POINTS}
     */
    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.POINTS;
    }
}
