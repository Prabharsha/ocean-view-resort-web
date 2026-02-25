package com.oceanview.resort.service.strategy;

import com.oceanview.resort.model.enums.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Concrete Strategy for processing <b>credit/debit card</b> payments.
 *
 * <p><b>Design Pattern: Strategy Pattern – Concrete Strategy</b></p>
 * <p>Implements the {@link PaymentStrategy} interface for card-based transactions.
 * In a production system this would integrate with a payment gateway (e.g.,
 * Stripe, PayPal). Here it simulates authorisation and generates a unique
 * card authorisation code.</p>
 *
 * <h3>Processing Logic:</h3>
 * <ol>
 *   <li>Validates the payment amount is positive.</li>
 *   <li>Validates that a transaction reference (card number / token) is provided.</li>
 *   <li>Simulates card authorisation with the payment gateway.</li>
 *   <li>Returns a successful result with an authorisation code.</li>
 * </ol>
 *
 * @see PaymentStrategy
 * @see com.oceanview.resort.service.factory.PaymentStrategyFactory
 */
@Component
@Slf4j
public class CardPaymentStrategy implements PaymentStrategy {

    /**
     * Processes a card payment by simulating gateway authorisation.
     *
     * @param request the payment request details (must include transactionReference)
     * @return {@link PaymentResult} with success status and authorisation code
     */
    @Override
    public PaymentResult execute(PaymentRequest request) {
        log.info("Processing CARD payment of {} for bill {}",
                request.getAmount(), request.getBillId());

        // Validate amount
        if (request.getAmount() == null || request.getAmount().signum() <= 0) {
            return PaymentResult.failure("Card payment amount must be positive");
        }

        // Validate card reference
        if (request.getTransactionReference() == null || request.getTransactionReference().isBlank()) {
            return PaymentResult.failure("Card transaction reference (card token) is required");
        }

        // Simulate card authorisation with payment gateway
        String authCode = "AUTH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("Card authorisation successful. Auth code: {}", authCode);

        return PaymentResult.success(
                authCode,
                request.getAmount(),
                "Card payment of " + request.getAmount() + " authorised. Auth Code: " + authCode
        );
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link PaymentMethod#CARD}
     */
    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.CARD;
    }
}
