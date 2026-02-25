package com.oceanview.resort.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link BillCalculator} following TDD (RED → GREEN → REFACTOR).
 *
 * <h3>Test Plan Document</h3>
 * <table>
 *   <tr><th>Test ID</th><th>Description</th><th>Preconditions</th><th>Steps</th><th>Expected</th><th>Status</th></tr>
 *   <tr><td>BCT-01</td><td>calculateTotal with various inputs (parameterized)</td>
 *       <td>BillCalculator instance</td>
 *       <td>Call calculateTotal with @CsvSource data</td><td>Returns correct total</td><td>PASS</td></tr>
 *   <tr><td>BCT-02</td><td>calculateSubtotal</td>
 *       <td>BillCalculator instance</td>
 *       <td>Call calculateSubtotal(nights, rate)</td><td>Returns nights × rate</td><td>PASS</td></tr>
 *   <tr><td>BCT-03</td><td>calculateTax</td>
 *       <td>BillCalculator instance</td>
 *       <td>Call calculateTax(subtotal, taxRate)</td><td>Returns subtotal × taxRate/100</td><td>PASS</td></tr>
 *   <tr><td>BCT-04</td><td>Single night stay</td>
 *       <td>BillCalculator instance</td>
 *       <td>Call calculateTotal(1, rate, tax, 0)</td><td>Returns correct total for 1 night</td><td>PASS</td></tr>
 *   <tr><td>BCT-05</td><td>Zero discount</td>
 *       <td>BillCalculator instance</td>
 *       <td>Call calculateTotal with discount = 0</td><td>Total equals subtotal + tax</td><td>PASS</td></tr>
 *   <tr><td>BCT-06</td><td>Large discount</td>
 *       <td>BillCalculator instance</td>
 *       <td>Call calculateTotal with large discount</td><td>Discount subtracted from total</td><td>PASS</td></tr>
 * </table>
 */
class BillCalculatorTest {

    private BillCalculator billCalculator;

    @BeforeEach
    void setUp() {
        billCalculator = new BillCalculator();
    }

    // ═══════════════════════════════════════════════════════════
    // BCT-01: Parameterized calculateTotal with various inputs
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: BCT-01
     * Description: Parameterized test covering multiple input combinations.
     * Preconditions: BillCalculator instance ready.
     * Steps: Call calculateTotal with each set of @CsvSource parameters.
     * Expected Result: Result matches the expected total for each row.
     * Actual Result: All rows match — PASS.
     *
     * Calculation formula: total = (nights × rate) + ((nights × rate) × taxRate / 100) - discount
     * Row 1: (3 × 150) + (450 × 10/100) - 0    = 450 + 45 - 0    = 495.00
     * Row 2: (5 × 200) + (1000 × 10/100) - 50   = 1000 + 100 - 50 = 1050.00
     * Row 3: (1 × 350) + (350 × 15/100) - 0     = 350 + 52.50 - 0 = 402.50
     * Row 4: (7 × 100) + (700 × 10/100) - 100   = 700 + 70 - 100  = 670.00
     */
    @ParameterizedTest(name = "{index}: {0} nights × ${1}, tax {2}%, discount ${3} = ${4}")
    @CsvSource({
            "3, 150.00, 10.00, 0.00, 495.00",
            "5, 200.00, 10.00, 50.00, 1050.00",
            "1, 350.00, 15.00, 0.00, 402.50",
            "7, 100.00, 10.00, 100.00, 670.00"
    })
    @DisplayName("BCT-01: calculateTotal with various inputs returns correct total")
    void calculateTotal_VariousInputs_ReturnsCorrectTotal(
            int nights, BigDecimal rate, BigDecimal taxRate,
            BigDecimal discount, BigDecimal expected) {

        // Act
        BigDecimal result = billCalculator.calculateTotal(nights, rate, taxRate, discount);

        // Assert
        assertThat(result).isEqualByComparingTo(expected);
    }

    // ═══════════════════════════════════════════════════════════
    // BCT-02: calculateSubtotal
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: BCT-02
     * Description: Subtotal equals nights × rate.
     * Preconditions: BillCalculator instance.
     * Steps: Call calculateSubtotal(3, 150.00).
     * Expected Result: 450.00.
     * Actual Result: 450.00 — PASS.
     */
    @Test
    @DisplayName("BCT-02: calculateSubtotal returns nights × rate")
    void calculateSubtotal_ReturnsCorrectAmount() {
        // Act
        BigDecimal result = billCalculator.calculateSubtotal(3, new BigDecimal("150.00"));

        // Assert
        assertThat(result).isEqualByComparingTo(new BigDecimal("450.00"));
    }

    // ═══════════════════════════════════════════════════════════
    // BCT-03: calculateTax
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: BCT-03
     * Description: Tax equals subtotal × (taxRate / 100).
     * Preconditions: BillCalculator instance, known subtotal.
     * Steps: Call calculateTax(1000.00, 10.00).
     * Expected Result: 100.00.
     * Actual Result: 100.00 — PASS.
     */
    @Test
    @DisplayName("BCT-03: calculateTax returns correct tax amount")
    void calculateTax_ReturnsCorrectTax() {
        // Act
        BigDecimal result = billCalculator.calculateTax(
                new BigDecimal("1000.00"), new BigDecimal("10.00"));

        // Assert
        assertThat(result).isEqualByComparingTo(new BigDecimal("100.00"));
    }

    // ═══════════════════════════════════════════════════════════
    // BCT-04: Single night stay
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: BCT-04
     * Description: Total for 1 night with 15% tax and no discount.
     * Preconditions: BillCalculator instance.
     * Steps: Call calculateTotal(1, 350.00, 15.00, 0.00).
     * Expected Result: 350 + 52.50 = 402.50.
     * Actual Result: 402.50 — PASS.
     */
    @Test
    @DisplayName("BCT-04: single night stay calculates correctly")
    void calculateTotal_SingleNight_ReturnsCorrectTotal() {
        // Act
        BigDecimal result = billCalculator.calculateTotal(
                1, new BigDecimal("350.00"), new BigDecimal("15.00"), BigDecimal.ZERO);

        // Assert
        assertThat(result).isEqualByComparingTo(new BigDecimal("402.50"));
    }

    // ═══════════════════════════════════════════════════════════
    // BCT-05: Zero discount
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: BCT-05
     * Description: With zero discount, total = subtotal + tax.
     * Preconditions: BillCalculator instance.
     * Steps: Call calculateTotal(5, 200.00, 10.00, 0.00).
     * Expected Result: 1000 + 100 = 1100.00.
     * Actual Result: 1100.00 — PASS.
     */
    @Test
    @DisplayName("BCT-05: zero discount means total = subtotal + tax")
    void calculateTotal_ZeroDiscount_EqualsSubtotalPlusTax() {
        // Act
        BigDecimal result = billCalculator.calculateTotal(
                5, new BigDecimal("200.00"), new BigDecimal("10.00"), BigDecimal.ZERO);

        // Assert
        BigDecimal subtotal = billCalculator.calculateSubtotal(5, new BigDecimal("200.00"));
        BigDecimal tax = billCalculator.calculateTax(subtotal, new BigDecimal("10.00"));
        assertThat(result).isEqualByComparingTo(subtotal.add(tax));
    }

    // ═══════════════════════════════════════════════════════════
    // BCT-06: Large discount
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: BCT-06
     * Description: Large discount is properly subtracted from total.
     * Preconditions: BillCalculator instance.
     * Steps: Call calculateTotal(7, 100.00, 10.00, 100.00).
     * Expected Result: 700 + 70 - 100 = 670.00.
     * Actual Result: 670.00 — PASS.
     */
    @Test
    @DisplayName("BCT-06: large discount subtracted correctly")
    void calculateTotal_LargeDiscount_SubtractsCorrectly() {
        // Act
        BigDecimal result = billCalculator.calculateTotal(
                7, new BigDecimal("100.00"), new BigDecimal("10.00"), new BigDecimal("100.00"));

        // Assert
        assertThat(result).isEqualByComparingTo(new BigDecimal("670.00"));
    }
}
