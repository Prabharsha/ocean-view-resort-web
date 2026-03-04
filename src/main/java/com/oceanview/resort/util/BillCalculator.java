package com.oceanview.resort.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utility class for bill calculations. All monetary calculations use BigDecimal.
 *
 * <p><b>Design Pattern: Singleton (single instance used app-wide)</b></p>
 */
public class BillCalculator {

    private static final int MONETARY_SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    public BigDecimal calculateTotal(int numNights, BigDecimal ratePerNight,
                                     BigDecimal taxRate, BigDecimal discount) {
        BigDecimal subtotal = calculateSubtotal(numNights, ratePerNight);
        BigDecimal taxAmount = calculateTax(subtotal, taxRate);
        return subtotal.add(taxAmount).subtract(discount).setScale(MONETARY_SCALE, ROUNDING_MODE);
    }

    public BigDecimal calculateSubtotal(int numNights, BigDecimal ratePerNight) {
        return ratePerNight.multiply(BigDecimal.valueOf(numNights)).setScale(MONETARY_SCALE, ROUNDING_MODE);
    }

    public BigDecimal calculateTax(BigDecimal subtotal, BigDecimal taxRate) {
        return subtotal.multiply(taxRate).divide(BigDecimal.valueOf(100), MONETARY_SCALE, ROUNDING_MODE);
    }
}

