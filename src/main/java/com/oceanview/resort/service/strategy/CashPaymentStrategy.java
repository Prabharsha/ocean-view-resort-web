package com.oceanview.resort.service.strategy;

import com.oceanview.resort.model.enums.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Concrete Strategy for processing <b>cash</b> payments.
 *
 * <p><b>Design Pattern: Strategy Pattern – Concrete Strategy</b></p>
 * <p>Implements the {@link PaymentStrategy} interface for cash-based transactions.
 * Cash payments are processed immediately at the front desk with a generated
 * receipt number as the transaction reference.</p>
 *
 * <h3>Processing Logic:</h3>
 * <ol>
 *   <li>Validates the payment amount is positive.</li>
 *   <li>Generates a unique cash receipt number (CASH-{UUID-prefix}).</li>
 *   <li>Returns a successful result with the receipt reference.</li>
 * </ol>
 *
 * @see PaymentStrategy
 * @see com.oceanview.resort.service.factory.PaymentStrategyFactory
 */
@Component
@Slf4j
public class CashPaymentStrategy implements PaymentStrategy {

    /**
     * Processes a cash payment by generating a receipt reference.
     *
     * @param request the payment request details
     * @return {@link PaymentResult} with success status and receipt number
     */
    @Override
    public PaymentResult execute(PaymentRequest request) {
        log.info("Processing CASH payment of {} for bill {}",
                request.getAmount(), request.getBillId());

        // Validate amount
        if (request.getAmount() == null || request.getAmount().signum() <= 0) {
            return PaymentResult.failure("Cash payment amount must be positive");
        }

        // Generate cash receipt reference
        String receiptNumber = "CASH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        log.info("Cash payment processed successfully. Receipt: {}", receiptNumber);

        return PaymentResult.success(
                receiptNumber,
                request.getAmount(),
                "Cash payment of " + request.getAmount() + " received at front desk. Receipt: " + receiptNumber
        );
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link PaymentMethod#CASH}
     */
    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.CASH;
    }
}
