package com.oceanview.resort.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utility class for bill calculations.
 * All monetary calculations use BigDecimal to ensure precision.
 *
 * <p><b>Design Pattern: Builder Pattern (in conjunction with Bill entity)</b></p>
 * <p>The Builder Pattern is used extensively in this project via Lombok's
 * {@code @Builder} annotation on model classes. This calculator works with
 * the Bill entity's builder to construct properly calculated bill objects:</p>
 * <pre>{@code
 * Bill bill = Bill.builder()
 *     .reservation(reservation)
 *     .numNights(3)
 *     .roomRate(new BigDecimal("150.00"))
 *     .subtotal(billCalculator.calculateSubtotal(3, new BigDecimal("150.00")))
 *     .taxRate(new BigDecimal("10.00"))
 *     .taxAmount(billCalculator.calculateTax(subtotal, new BigDecimal("10.00")))
 *     .totalAmount(billCalculator.calculateTotal(3, rate, taxRate, discount))
 *     .paymentStatus("PENDING")
 *     .build();
 * }</pre>
 *
 * <p><b>Singleton Pattern:</b> {@code @Component} makes this a Spring singleton.</p>
 */
@Component
public class BillCalculator {

    private static final int MONETARY_SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * Calculates the total bill amount.
     *
     * @param numNights    number of nights stayed
     * @param ratePerNight nightly room rate
     * @param taxRate      tax rate as a percentage (e.g., 10.00 for 10%)
     * @param discount     discount amount to subtract
     * @return the total bill amount
     */
    public BigDecimal calculateTotal(int numNights, BigDecimal ratePerNight,
                                     BigDecimal taxRate, BigDecimal discount) {
        BigDecimal subtotal = calculateSubtotal(numNights, ratePerNight);
        BigDecimal taxAmount = calculateTax(subtotal, taxRate);
        return subtotal.add(taxAmount).subtract(discount)
                .setScale(MONETARY_SCALE, ROUNDING_MODE);
    }

    /**
     * Calculates the subtotal (nights x rate).
     *
     * @param numNights    number of nights
     * @param ratePerNight nightly rate
     * @return subtotal amount
     */
    public BigDecimal calculateSubtotal(int numNights, BigDecimal ratePerNight) {
        return ratePerNight.multiply(BigDecimal.valueOf(numNights))
                .setScale(MONETARY_SCALE, ROUNDING_MODE);
    }

    /**
     * Calculates the tax amount.
     *
     * @param subtotal subtotal amount
     * @param taxRate  tax rate as a percentage
     * @return tax amount
     */
    public BigDecimal calculateTax(BigDecimal subtotal, BigDecimal taxRate) {
        return subtotal.multiply(taxRate)
                .divide(BigDecimal.valueOf(100), MONETARY_SCALE, ROUNDING_MODE);
    }
}
