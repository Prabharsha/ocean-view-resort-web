package com.oceanview.resort.event.listener;

import com.oceanview.resort.event.CheckInEvent;
import com.oceanview.resort.event.CheckOutEvent;
import com.oceanview.resort.event.ReservationCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Observer/Listener that records audit log entries for reservation events.
 *
 * <p><b>Design Pattern: Observer Pattern – Concrete Observer</b></p>
 * <p>This listener observes reservation lifecycle events and creates audit trail
 * records. It demonstrates the Observer Pattern's ability to have multiple
 * independent listeners responding to the same events — while
 * {@link EmailNotificationListener} sends emails, this listener logs audit
 * entries for compliance and traceability.</p>
 *
 * <h3>Events handled:</h3>
 * <ul>
 *   <li>{@link ReservationCreatedEvent} – Logs reservation creation with details.</li>
 *   <li>{@link CheckInEvent} – Logs guest check-in timestamp and details.</li>
 *   <li>{@link CheckOutEvent} – Logs guest check-out timestamp and details.</li>
 * </ul>
 *
 * <h3>Audit Log Format:</h3>
 * <pre>[AUDIT] {TIMESTAMP} | {EVENT_TYPE} | Reservation: {NUMBER} | Details: {INFO}</pre>
 *
 * <p><i>Note: In a production system, audit entries would be persisted to an
 * {@code audit_log} database table. Here they are written to the application
 * log for demonstration purposes.</i></p>
 *
 * @see ReservationCreatedEvent
 * @see CheckInEvent
 * @see CheckOutEvent
 * @see EmailNotificationListener
 */
@Component
@Slf4j
public class AuditLogListener {

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Records an audit entry when a reservation is created.
     *
     * <p><b>Observer Pattern:</b> Automatically invoked by Spring's event
     * mechanism when a {@link ReservationCreatedEvent} is published by
     * the ReservationServiceImpl (subject).</p>
     *
     * @param event the reservation created event
     */
    @EventListener
    public void onReservationCreated(ReservationCreatedEvent event) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        log.info("[AUDIT] {} | RESERVATION_CREATED | Reservation: {} | Guest: {} | Room: {} | Check-in: {} | Check-out: {}",
                timestamp,
                event.getReservation().getReservationNumber(),
                event.getReservation().getGuestName(),
                event.getReservation().getRoomId(),
                event.getReservation().getCheckInDate(),
                event.getReservation().getCheckOutDate());
    }

    /**
     * Records an audit entry when a guest checks in.
     *
     * @param event the check-in event
     */
    @EventListener
    public void onCheckIn(CheckInEvent event) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        log.info("[AUDIT] {} | GUEST_CHECK_IN | Reservation: {} | Guest: {} | Room: {}",
                timestamp,
                event.getReservation().getReservationNumber(),
                event.getReservation().getGuestName(),
                event.getReservation().getRoomId());
    }

    /**
     * Records an audit entry when a guest checks out.
     *
     * @param event the check-out event
     */
    @EventListener
    public void onCheckOut(CheckOutEvent event) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        log.info("[AUDIT] {} | GUEST_CHECK_OUT | Reservation: {} | Guest: {} | Room: {}",
                timestamp,
                event.getReservation().getReservationNumber(),
                event.getReservation().getGuestName(),
                event.getReservation().getRoomId());
    }
}
