package com.oceanview.resort.service.strategy;

import com.oceanview.resort.model.enums.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Concrete Strategy for processing <b>online</b> payments via payment gateway.
 *
 * <p><b>Design Pattern: Strategy Pattern – Concrete Strategy</b></p>
 * <p>Implements the {@link PaymentStrategy} interface for online payment
 * transactions. In a production system this would integrate with external
 * payment gateways (e.g., Stripe, PayPal, Worldpay). Here it simulates
 * the gateway interaction and generates a unique online transaction ID.</p>
 *
 * <h3>Processing Logic:</h3>
 * <ol>
 *   <li>Validates the payment amount is positive.</li>
 *   <li>Validates that a transaction reference (payment token) is provided.</li>
 *   <li>Simulates communication with the online payment gateway.</li>
 *   <li>Returns a successful result with an online transaction ID.</li>
 * </ol>
 *
 * @see PaymentStrategy
 * @see com.oceanview.resort.service.factory.PaymentStrategyFactory
 */
@Component
@Slf4j
public class OnlinePaymentStrategy implements PaymentStrategy {

    /**
     * Processes an online payment by simulating gateway communication.
     *
     * @param request the payment request details (must include transactionReference)
     * @return {@link PaymentResult} with success status and online transaction ID
     */
    @Override
    public PaymentResult execute(PaymentRequest request) {
        log.info("Processing ONLINE payment of {} for bill {}",
                request.getAmount(), request.getBillId());

        // Validate amount
        if (request.getAmount() == null || request.getAmount().signum() <= 0) {
            return PaymentResult.failure("Online payment amount must be positive");
        }

        // Validate payment token / reference
        if (request.getTransactionReference() == null || request.getTransactionReference().isBlank()) {
            return PaymentResult.failure("Online payment token is required for gateway processing");
        }

        // Simulate online payment gateway processing
        String onlineTxnId = "ONL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("Online payment gateway authorised. Transaction ID: {}", onlineTxnId);

        return PaymentResult.success(
                onlineTxnId,
                request.getAmount(),
                "Online payment of " + request.getAmount()
                        + " processed via payment gateway. Transaction ID: " + onlineTxnId
        );
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link PaymentMethod#ONLINE}
     */
    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.ONLINE;
    }
}
