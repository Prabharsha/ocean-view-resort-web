package com.oceanview.resort.service;

import com.oceanview.resort.dto.ReservationDTO;

/**
 * Service interface for sending email notifications.
 *
 * <p>Abstracts email sending logic so that the event listeners and
 * scheduled tasks can send richly formatted HTML emails with
 * Thymeleaf templates and optional PDF attachments.</p>
 */
public interface EmailService {

    /**
     * Sends a booking confirmation email to the guest.
     *
     * @param reservation the reservation details
     */
    void sendBookingConfirmation(ReservationDTO reservation);

    /**
     * Sends a check-in welcome email to the guest.
     *
     * @param reservation the reservation details
     */
    void sendCheckInWelcome(ReservationDTO reservation);

    /**
     * Sends a thank-you / check-out email to the guest.
     *
     * @param reservation the reservation details
     */
    void sendCheckOutThankYou(ReservationDTO reservation);

    /**
     * Sends a check-in reminder email (sent the day before check-in).
     *
     * @param reservation the reservation details
     */
    void sendCheckInReminder(ReservationDTO reservation);

    /**
     * Sends a bill receipt email to the guest.
     *
     * @param guestEmail the guest's email address
     * @param guestName  the guest's name
     * @param billId     the bill ID
     * @param totalAmount the total bill amount
     */
    void sendBillReceipt(String guestEmail, String guestName,
                         String billId, String totalAmount);
}
