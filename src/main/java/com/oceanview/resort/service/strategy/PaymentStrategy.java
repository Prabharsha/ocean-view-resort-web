package com.oceanview.resort.service.strategy;

import com.oceanview.resort.model.enums.PaymentMethod;

/**
 * Strategy interface for processing payments at Ocean View Resort.
 *
 * <p><b>Design Pattern: Strategy Pattern</b></p>
 * <p>The Strategy Pattern defines a family of algorithms (payment processing methods),
 * encapsulates each one in a separate class, and makes them interchangeable.
 * This allows the payment processing logic to vary independently from
 * the clients that use it (e.g., {@code PaymentServiceImpl}).</p>
 *
 * <h3>How it works in this system:</h3>
 * <ol>
 *   <li><b>Interface:</b> {@code PaymentStrategy} defines the contract with
 *       {@link #execute(PaymentRequest)} and {@link #getPaymentMethod()}.</li>
 *   <li><b>Concrete Strategies:</b>
 *       <ul>
 *         <li>{@code CashPaymentStrategy} – Handles cash payments at front desk.</li>
 *         <li>{@code CardPaymentStrategy} – Handles credit/debit card payments.</li>
 *         <li>{@code PointsPaymentStrategy} – Handles loyalty points redemption.</li>
 *         <li>{@code OnlinePaymentStrategy} – Handles online payment gateway.</li>
 *       </ul>
 *   </li>
 *   <li><b>Context:</b> {@code PaymentServiceImpl} uses the strategy selected by
 *       {@code PaymentStrategyFactory} based on the {@link PaymentMethod} enum.</li>
 * </ol>
 *
 * <h3>Benefits:</h3>
 * <ul>
 *   <li>Open/Closed Principle: new payment methods can be added without modifying
 *       existing code.</li>
 *   <li>Single Responsibility: each strategy class handles only its own payment logic.</li>
 *   <li>Eliminates complex if-else/switch blocks in the payment service.</li>
 * </ul>
 *
 * @see CashPaymentStrategy
 * @see CardPaymentStrategy
 * @see PointsPaymentStrategy
 * @see OnlinePaymentStrategy
 * @see com.oceanview.resort.service.factory.PaymentStrategyFactory
 */
public interface PaymentStrategy {

    /**
     * Executes the payment using the specific strategy logic.
     *
     * @param request the payment request containing all necessary details
     * @return the result of the payment processing
     */
    PaymentResult execute(PaymentRequest request);

    /**
     * Returns the payment method this strategy handles.
     *
     * @return the {@link PaymentMethod} enum constant
     */
    PaymentMethod getPaymentMethod();
}
