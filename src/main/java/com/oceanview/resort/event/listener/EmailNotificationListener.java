package com.oceanview.resort.event.listener;

import com.oceanview.resort.dto.ReservationDTO;
import com.oceanview.resort.event.CheckInEvent;
import com.oceanview.resort.event.CheckOutEvent;
import com.oceanview.resort.event.ReservationCreatedEvent;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.enums.ReservationStatus;
import com.oceanview.resort.repository.ReservationRepository;
import com.oceanview.resort.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Observer/Listener that sends email notifications in response to reservation events.
 *
 * <p><b>Design Pattern: Observer Pattern – Concrete Observer</b></p>
 * <p>This listener observes reservation lifecycle events and triggers email
 * notifications to guests. It is decoupled from the service layer — the
 * {@code ReservationServiceImpl} (subject) publishes events without knowing
 * which listeners are subscribed.</p>
 *
 * <h3>Events handled:</h3>
 * <ul>
 *   <li>{@link ReservationCreatedEvent} – Sends a booking confirmation email.</li>
 *   <li>{@link CheckInEvent} – Sends a welcome / check-in confirmation email.</li>
 *   <li>{@link CheckOutEvent} – Sends a thank-you / feedback request email.</li>
 * </ul>
 *
 * <h3>Scheduled tasks:</h3>
 * <ul>
 *   <li>Check-in reminders – Runs daily at 9 AM, sends reminder emails to guests
 *       whose check-in date is the next day.</li>
 * </ul>
 *
 * <h3>Spring Event Mechanism:</h3>
 * <p>Uses {@code @EventListener} annotation for automatic event subscription.
 * Methods annotated with {@code @Async} run in a separate thread pool so that
 * email sending does not block the main transaction.</p>
 *
 * @see ReservationCreatedEvent
 * @see CheckInEvent
 * @see CheckOutEvent
 * @see AuditLogListener
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationListener {

    private final EmailService emailService;
    private final ReservationRepository reservationRepository;

    /**
     * Handles the reservation creation event by sending a confirmation email.
     *
     * <p><b>Observer Pattern:</b> This method is automatically invoked by Spring's
     * event dispatcher when a {@link ReservationCreatedEvent} is published.</p>
     *
     * @param event the reservation created event
     */
    @EventListener
    @Async
    public void onReservationCreated(ReservationCreatedEvent event) {
        log.info("[EMAIL] Sending booking confirmation email for reservation: {}",
                event.getReservation().getReservationNumber());

        emailService.sendBookingConfirmation(event.getReservation());

        log.info("[EMAIL] Booking confirmation email sent successfully for reservation: {}",
                event.getReservation().getReservationNumber());
    }

    /**
     * Handles the check-in event by sending a welcome email.
     *
     * @param event the check-in event
     */
    @EventListener
    @Async
    public void onCheckIn(CheckInEvent event) {
        log.info("[EMAIL] Sending welcome email for check-in. Reservation: {}",
                event.getReservation().getReservationNumber());

        emailService.sendCheckInWelcome(event.getReservation());

        log.info("[EMAIL] Welcome email sent for reservation: {}",
                event.getReservation().getReservationNumber());
    }

    /**
     * Handles the check-out event by sending a thank-you email.
     *
     * @param event the check-out event
     */
    @EventListener
    @Async
    public void onCheckOut(CheckOutEvent event) {
        log.info("[EMAIL] Sending thank-you email for check-out. Reservation: {}",
                event.getReservation().getReservationNumber());

        emailService.sendCheckOutThankYou(event.getReservation());

        log.info("[EMAIL] Thank-you email sent for reservation: {}",
                event.getReservation().getReservationNumber());
    }

    /**
     * Scheduled task: sends check-in reminder emails to guests whose
     * check-in date is tomorrow. Runs daily at 9:00 AM.
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void sendCheckInReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        log.info("[EMAIL] Running scheduled check-in reminder job for date: {}", tomorrow);

        List<Reservation> reservations = reservationRepository
                .findByCheckInDateBetween(tomorrow, tomorrow);

        reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .forEach(r -> {
                    try {
                        ReservationDTO dto = ReservationDTO.builder()
                                .reservationNumber(r.getReservationNumber())
                                .guestName(r.getGuestName())
                                .guestContact(r.getGuestContact())
                                .checkInDate(r.getCheckInDate())
                                .checkOutDate(r.getCheckOutDate())
                                .roomNumber(r.getRoom() != null ? r.getRoom().getRoomNumber() : "TBA")
                                .roomType(r.getRoom() != null ? r.getRoom().getRoomType().getDisplayName() : "")
                                .build();
                        emailService.sendCheckInReminder(dto);
                        log.info("[EMAIL] Check-in reminder sent for reservation: {}",
                                r.getReservationNumber());
                    } catch (Exception e) {
                        log.error("[EMAIL] Failed to send reminder for reservation {}: {}",
                                r.getReservationNumber(), e.getMessage());
                    }
                });

        log.info("[EMAIL] Check-in reminder job completed. Processed {} reservations",
                reservations.size());
    }
}
