package com.oceanview.resort.event;

import com.oceanview.resort.dto.ReservationDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event published when a new reservation is created at Ocean View Resort.
 *
 * <p><b>Design Pattern: Observer Pattern (Event)</b></p>
 * <p>The Observer Pattern defines a one-to-many dependency between objects so
 * that when one object changes state, all its dependents (observers/listeners)
 * are notified and updated automatically.</p>
 *
 * <h3>How it works in this system:</h3>
 * <ul>
 *   <li><b>Subject/Publisher:</b> {@code ReservationServiceImpl} publishes events
 *       using Spring's {@code ApplicationEventPublisher}.</li>
 *   <li><b>Events:</b> {@code ReservationCreatedEvent}, {@code CheckInEvent},
 *       {@code CheckOutEvent} — each carries relevant reservation data.</li>
 *   <li><b>Observers/Listeners:</b>
 *       {@link com.oceanview.resort.event.listener.EmailNotificationListener} and
 *       {@link com.oceanview.resort.event.listener.AuditLogListener} respond
 *       to these events asynchronously.</li>
 * </ul>
 *
 * <h3>Benefits of using Spring ApplicationEvent:</h3>
 * <ul>
 *   <li>Loose coupling: the publisher doesn't know about the listeners.</li>
 *   <li>Extensibility: new listeners can be added without modifying the publisher.</li>
 *   <li>Spring manages listener discovery and invocation automatically.</li>
 * </ul>
 *
 * @see CheckInEvent
 * @see CheckOutEvent
 * @see com.oceanview.resort.event.listener.EmailNotificationListener
 * @see com.oceanview.resort.event.listener.AuditLogListener
 */
@Getter
public class ReservationCreatedEvent extends ApplicationEvent {

    /** The reservation data associated with this event. */
    private final ReservationDTO reservation;

    /**
     * Creates a new ReservationCreatedEvent.
     *
     * @param source      the object that published this event
     * @param reservation the newly created reservation DTO
     */
    public ReservationCreatedEvent(Object source, ReservationDTO reservation) {
        super(source);
        this.reservation = reservation;
    }
}
