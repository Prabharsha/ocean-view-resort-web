package com.oceanview.resort.service.factory;

import com.oceanview.resort.model.enums.PaymentMethod;
import com.oceanview.resort.service.strategy.PaymentStrategy;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Factory for creating the appropriate {@link PaymentStrategy} based on
 * the selected {@link PaymentMethod}.
 *
 * <p><b>Design Pattern: Factory Pattern</b></p>
 * <p>The Factory Pattern provides an interface for creating objects without
 * specifying their concrete classes. {@code PaymentStrategyFactory} encapsulates
 * the logic to determine which {@link PaymentStrategy} implementation should
 * be used for a given {@link PaymentMethod} enum value.</p>
 *
 * <h3>How it works in this system:</h3>
 * <ol>
 *   <li><b>Spring Auto-Discovery:</b> All concrete {@link PaymentStrategy}
 *       implementations are Spring {@code @Component}s and are injected as a
 *       {@code List<PaymentStrategy>} through constructor injection.</li>
 *   <li><b>Registry Map:</b> On initialization ({@code @PostConstruct}), the
 *       factory builds an {@link EnumMap} that maps each {@link PaymentMethod}
 *       to its corresponding strategy instance.</li>
 *   <li><b>Lookup:</b> Clients call {@link #getStrategy(PaymentMethod)} to
 *       obtain the correct strategy without needing to know the concrete class.</li>
 * </ol>
 *
 * <h3>Benefits:</h3>
 * <ul>
 *   <li><b>Open/Closed Principle:</b> Adding a new payment method only requires
 *       creating a new {@code PaymentStrategy} implementation annotated with
 *       {@code @Component} — no changes to this factory are needed.</li>
 *   <li><b>Encapsulation:</b> The complexity of choosing the right strategy is
 *       hidden behind a single factory method.</li>
 *   <li><b>Testability:</b> The factory can be easily mocked in unit tests.</li>
 * </ul>
 *
 * <h3>Usage example:</h3>
 * <pre>{@code
 * PaymentStrategy strategy = paymentStrategyFactory.getStrategy(PaymentMethod.CASH);
 * PaymentResult result = strategy.execute(paymentRequest);
 * }</pre>
 *
 * @see PaymentStrategy
 * @see PaymentMethod
 */
@Component
@Slf4j
public class PaymentStrategyFactory {

    /** All available payment strategy implementations, injected by Spring. */
    private final List<PaymentStrategy> strategies;

    /** Registry mapping each PaymentMethod to its strategy implementation. */
    private final Map<PaymentMethod, PaymentStrategy> strategyMap = new EnumMap<>(PaymentMethod.class);

    /**
     * Constructs the factory with all available payment strategy beans.
     *
     * @param strategies list of all {@link PaymentStrategy} implementations
     *                   discovered by Spring component scanning
     */
    public PaymentStrategyFactory(List<PaymentStrategy> strategies) {
        this.strategies = strategies;
    }

    /**
     * Initializes the strategy registry by mapping each strategy to its
     * {@link PaymentMethod}. Called automatically after dependency injection.
     */
    @PostConstruct
    public void initStrategyMap() {
        for (PaymentStrategy strategy : strategies) {
            strategyMap.put(strategy.getPaymentMethod(), strategy);
            log.info("Registered payment strategy: {} -> {}",
                    strategy.getPaymentMethod(), strategy.getClass().getSimpleName());
        }
        log.info("PaymentStrategyFactory initialized with {} strategies", strategyMap.size());
    }

    /**
     * Returns the appropriate {@link PaymentStrategy} for the given payment method.
     *
     * <p><b>Factory Method:</b> This is the core factory method that produces the
     * correct strategy object based on the provided {@link PaymentMethod}.</p>
     *
     * @param method the payment method to look up
     * @return the corresponding {@link PaymentStrategy} implementation
     * @throws IllegalArgumentException if no strategy is registered for the given method
     */
    public PaymentStrategy getStrategy(PaymentMethod method) {
        PaymentStrategy strategy = strategyMap.get(method);
        if (strategy == null) {
            throw new IllegalArgumentException(
                    "No payment strategy registered for method: " + method);
        }
        log.debug("Factory returning strategy: {} for method: {}",
                strategy.getClass().getSimpleName(), method);
        return strategy;
    }

    /**
     * Checks whether a strategy is registered for the given payment method.
     *
     * @param method the payment method to check
     * @return {@code true} if a strategy exists for this method
     */
    public boolean hasStrategy(PaymentMethod method) {
        return strategyMap.containsKey(method);
    }
}
