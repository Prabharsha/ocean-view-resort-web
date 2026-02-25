package com.oceanview.resort.service.impl;

import com.oceanview.resort.dto.ReservationDTO;
import com.oceanview.resort.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Implementation of {@link EmailService} using Spring Mail and Thymeleaf templates.
 *
 * <p>Sends richly formatted HTML emails for reservation lifecycle events.
 * Uses Thymeleaf's {@link TemplateEngine} to render HTML email templates
 * and {@link JavaMailSender} for SMTP delivery.</p>
 *
 * <p>If email sending fails (e.g., SMTP server unavailable), the error is
 * logged but NOT propagated — email failures must not block reservation
 * operations.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    /** Sender address for all outgoing emails. */
    private static final String FROM_ADDRESS = "noreply@oceanviewresort.com";
    private static final String RESORT_NAME = "Ocean View Resort";

    /** {@inheritDoc} */
    @Override
    public void sendBookingConfirmation(ReservationDTO reservation) {
        Context context = new Context();
        context.setVariable("guestName", reservation.getGuestName());
        context.setVariable("reservationNumber", reservation.getReservationNumber());
        context.setVariable("checkInDate", reservation.getCheckInDate());
        context.setVariable("checkOutDate", reservation.getCheckOutDate());
        context.setVariable("roomType", reservation.getRoomType());
        context.setVariable("roomNumber", reservation.getRoomNumber());
        context.setVariable("numGuests", reservation.getNumGuests());

        String htmlBody = templateEngine.process("email/booking-confirmation", context);
        sendHtmlEmail(reservation.getGuestContact(),
                RESORT_NAME + " - Booking Confirmation #" + reservation.getReservationNumber(),
                htmlBody);
    }

    /** {@inheritDoc} */
    @Override
    public void sendCheckInWelcome(ReservationDTO reservation) {
        Context context = new Context();
        context.setVariable("guestName", reservation.getGuestName());
        context.setVariable("reservationNumber", reservation.getReservationNumber());
        context.setVariable("roomNumber", reservation.getRoomNumber());

        String htmlBody = templateEngine.process("email/checkin-welcome", context);
        sendHtmlEmail(reservation.getGuestContact(),
                RESORT_NAME + " - Welcome! Check-In Confirmed",
                htmlBody);
    }

    /** {@inheritDoc} */
    @Override
    public void sendCheckOutThankYou(ReservationDTO reservation) {
        Context context = new Context();
        context.setVariable("guestName", reservation.getGuestName());
        context.setVariable("reservationNumber", reservation.getReservationNumber());

        String htmlBody = templateEngine.process("email/checkout-thankyou", context);
        sendHtmlEmail(reservation.getGuestContact(),
                RESORT_NAME + " - Thank You for Staying With Us!",
                htmlBody);
    }

    /** {@inheritDoc} */
    @Override
    public void sendCheckInReminder(ReservationDTO reservation) {
        Context context = new Context();
        context.setVariable("guestName", reservation.getGuestName());
        context.setVariable("reservationNumber", reservation.getReservationNumber());
        context.setVariable("checkInDate", reservation.getCheckInDate());
        context.setVariable("roomType", reservation.getRoomType());

        String htmlBody = templateEngine.process("email/checkin-reminder", context);
        sendHtmlEmail(reservation.getGuestContact(),
                RESORT_NAME + " - Check-In Reminder for Tomorrow",
                htmlBody);
    }

    /** {@inheritDoc} */
    @Override
    public void sendBillReceipt(String guestEmail, String guestName,
                                String billId, String totalAmount) {
        Context context = new Context();
        context.setVariable("guestName", guestName);
        context.setVariable("billId", billId);
        context.setVariable("totalAmount", totalAmount);

        String htmlBody = templateEngine.process("email/bill-receipt", context);
        sendHtmlEmail(guestEmail,
                RESORT_NAME + " - Payment Receipt",
                htmlBody);
    }

    /**
     * Sends an HTML email via SMTP. Errors are logged but not thrown.
     *
     * @param to      recipient email address
     * @param subject email subject line
     * @param html    HTML body content
     */
    private void sendHtmlEmail(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(FROM_ADDRESS);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            log.info("[EMAIL] Sent '{}' to {}", subject, to);
        } catch (MessagingException e) {
            log.error("[EMAIL] Failed to send '{}' to {}: {}", subject, to, e.getMessage());
        } catch (Exception e) {
            log.warn("[EMAIL] Email sending skipped (mail server may be unavailable): {}", e.getMessage());
        }
    }
}
