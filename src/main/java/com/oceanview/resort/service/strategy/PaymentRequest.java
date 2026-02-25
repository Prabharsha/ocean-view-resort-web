package com.oceanview.resort.service.strategy;

import com.oceanview.resort.model.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Encapsulates all the information needed to process a payment.
 *
 * <p><b>Design Pattern: Strategy Pattern (supporting class)</b></p>
 * <p>This request object is passed to each {@link PaymentStrategy} implementation,
 * decoupling the payment data from the processing logic. Each strategy implementation
 * can extract the fields it needs without knowing about other strategies.</p>
 *
 * @see PaymentStrategy
 * @see PaymentResult
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    /** The bill ID this payment is for. */
    private String billId;

    /** The reservation ID associated with this payment. */
    private String reservationId;

    /** The amount to be paid. */
    private BigDecimal amount;

    /** The payment method selected by the guest. */
    private PaymentMethod paymentMethod;

    /** External transaction reference (card auth code, online txn ID, etc.). */
    private String transactionReference;

    /** Optional notes about the payment. */
    private String notes;

    /** ID of the staff member processing this payment. */
    private String processedByStaffId;
}
