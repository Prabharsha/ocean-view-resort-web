package com.oceanview.resort.config;

import com.oceanview.resort.service.decorator.LoggingReservationService;
import com.oceanview.resort.service.interfaces.ReservationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class that wires the Decorator Pattern for services.
 *
 * <p><b>Design Pattern: Decorator Pattern (Configuration)</b></p>
 * <p>This configuration creates a {@link LoggingReservationService} bean that
 * wraps the original {@code ReservationServiceImpl}. The decorator is marked
 * as {@code @Primary}, so all injection points requiring {@link ReservationService}
 * will receive the decorated (logging-enabled) version automatically.</p>
 *
 * <h3>Bean wiring:</h3>
 * <pre>
 *   Controller → LoggingReservationService (decorated, @Primary)
 *                     ↓ delegates to
 *                ReservationServiceImpl (original @Service bean)
 * </pre>
 *
 * @see LoggingReservationService
 * @see com.oceanview.resort.service.impl.ReservationServiceImpl
 */
@Configuration
public class ServiceDecoratorConfig {

    /**
     * Creates the logging decorator wrapping the actual ReservationService implementation.
     *
     * <p>The {@code @Primary} annotation ensures this decorated bean is preferred
     * over the original {@code ReservationServiceImpl} when autowired by type.</p>
     *
     * @param reservationService the original ReservationService implementation
     * @return the decorated ReservationService with logging capabilities
     */
    @Bean
    @Primary
    public ReservationService loggingReservationService(
            @Qualifier("reservationServiceImpl") ReservationService reservationService) {
        return new LoggingReservationService(reservationService);
    }
}
