package com.oceanview.resort.util;

import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Singleton component for generating unique reservation numbers.
 *
 * <p><b>Design Pattern: Singleton Pattern</b></p>
 * <p>The Singleton Pattern ensures a class has only one instance and provides
 * a global point of access to it. In Spring, all beans are singletons by
 * default ({@code @Scope("singleton")}). The {@code @Component} annotation
 * registers this class as a Spring-managed singleton bean:</p>
 * <ul>
 *   <li>Spring creates exactly <b>one instance</b> of this class.</li>
 *   <li>The same instance is injected into all dependent beans.</li>
 *   <li>The singleton lifecycle is managed by the Spring IoC container.</li>
 * </ul>
 *
 * <h3>Thread Safety:</h3>
 * <p>Uses {@link AtomicLong} for the sequence counter to ensure thread-safe
 * generation of unique reservation numbers without explicit synchronization.</p>
 *
 * <h3>Number Format:</h3>
 * <pre>OVR-{YEAR}-{6-DIGIT-SEQUENCE}</pre>
 * <p>Example: {@code OVR-2026-000001}</p>
 *
 * <h3>Other Singleton Examples in this project:</h3>
 * <ul>
 *   <li>All {@code @Service} beans (e.g., ReservationServiceImpl, PaymentServiceImpl)</li>
 *   <li>All {@code @Repository} beans (e.g., ReservationRepository, RoomRepository)</li>
 *   <li>All {@code @Component} beans (e.g., JwtTokenProvider, PaymentStrategyFactory)</li>
 *   <li>All {@code @Configuration} beans (e.g., SecurityConfig)</li>
 * </ul>
 */
@Component
public class ReservationNumberGenerator {

    private final AtomicLong sequence = new AtomicLong(0);

    /**
     * Generates the next unique reservation number.
     *
     * @return a unique reservation number in the format OVR-YYYY-NNNNNN
     */
    public String generateNext() {
        long nextVal = sequence.incrementAndGet();
        return String.format("OVR-%d-%06d", Year.now().getValue(), nextVal);
    }

    /**
     * Sets the current sequence value (useful when initializing from database).
     *
     * @param value the sequence value to set
     */
    public void setSequence(long value) {
        sequence.set(value);
    }
}
