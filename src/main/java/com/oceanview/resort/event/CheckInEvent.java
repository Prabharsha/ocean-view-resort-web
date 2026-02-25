package com.oceanview.resort.event;

import com.oceanview.resort.dto.ReservationDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event published when a guest checks in to their reservation.
 *
 * <p><b>Design Pattern: Observer Pattern (Event)</b></p>
 * <p>Part of the Observer Pattern implementation using Spring's event system.
 * This event is published by {@code ReservationServiceImpl} when the
 * {@code checkIn()} method is called, notifying all registered listeners
 * (e.g., {@code EmailNotificationListener}, {@code AuditLogListener}).</p>
 *
 * @see ReservationCreatedEvent
 * @see CheckOutEvent
 * @see com.oceanview.resort.event.listener.EmailNotificationListener
 * @see com.oceanview.resort.event.listener.AuditLogListener
 */
@Getter
public class CheckInEvent extends ApplicationEvent {

    /** The reservation data for the check-in. */
    private final ReservationDTO reservation;

    /**
     * Creates a new CheckInEvent.
     *
     * @param source      the object that published this event
     * @param reservation the reservation DTO for the guest checking in
     */
    public CheckInEvent(Object source, ReservationDTO reservation) {
        super(source);
        this.reservation = reservation;
    }
}
