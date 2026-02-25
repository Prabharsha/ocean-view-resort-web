package com.oceanview.resort.event;

import com.oceanview.resort.dto.ReservationDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event published when a guest checks out from their reservation.
 *
 * <p><b>Design Pattern: Observer Pattern (Event)</b></p>
 * <p>Part of the Observer Pattern implementation using Spring's event system.
 * This event is published by {@code ReservationServiceImpl} when the
 * {@code checkOut()} method is called, notifying all registered listeners
 * (e.g., {@code EmailNotificationListener}, {@code AuditLogListener}).</p>
 *
 * @see ReservationCreatedEvent
 * @see CheckInEvent
 * @see com.oceanview.resort.event.listener.EmailNotificationListener
 * @see com.oceanview.resort.event.listener.AuditLogListener
 */
@Getter
public class CheckOutEvent extends ApplicationEvent {

    /** The reservation data for the check-out. */
    private final ReservationDTO reservation;

    /**
     * Creates a new CheckOutEvent.
     *
     * @param source      the object that published this event
     * @param reservation the reservation DTO for the guest checking out
     */
    public CheckOutEvent(Object source, ReservationDTO reservation) {
        super(source);
        this.reservation = reservation;
    }
}
