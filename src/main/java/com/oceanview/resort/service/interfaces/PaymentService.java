package com.oceanview.resort.service.interfaces;

import com.oceanview.resort.dto.PaymentDTO;

import java.util.List;

/**
 * Service interface for payment processing operations.
 * Provides methods for processing payments and retrieving payment history.
 */
public interface PaymentService {

    /**
     * Processes a payment against a bill.
     */
    PaymentDTO processPayment(PaymentDTO paymentDTO);

    /**
     * Finds all payments for a specific bill.
     */
    List<PaymentDTO> findByBillId(String billId);

    /**
     * Finds all payments for a specific reservation.
     */
    List<PaymentDTO> findByReservationId(String reservationId);

    /**
     * Finds a payment by its UUID.
     */
    PaymentDTO findById(String id);
}
