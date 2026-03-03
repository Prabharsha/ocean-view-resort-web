package com.oceanview.resort.service.interfaces;

import com.oceanview.resort.dto.BillDTO;
import com.oceanview.resort.dto.PaymentDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for bill management operations.
 * Provides methods for generating, querying, discounting, and managing bills.
 */
public interface BillService {

    /**
     * Generates a bill for a reservation, or returns the existing bill if one already exists.
     *
     * @param reservationId the reservation UUID
     * @return the generated or existing bill DTO
     */
    BillDTO generateOrGetBill(String reservationId);

    /**
     * Generates a bill for a completed or checked-out reservation.
     *
     * @param reservationId the reservation UUID
     * @return the generated bill DTO
     */
    BillDTO generateBill(String reservationId);

    /**
     * Finds a bill by the associated reservation ID.
     *
     * @param reservationId the reservation UUID
     * @return the bill DTO
     */
    BillDTO findBillByReservationId(String reservationId);

    /**
     * Finds a bill by its own UUID.
     *
     * @param billId the bill UUID
     * @return the bill DTO
     */
    BillDTO findBillById(String billId);

    /**
     * Applies a percentage discount to a bill.
     *
     * @param billId          the bill UUID
     * @param discountPercent the discount percentage (e.g. 10.00 for 10%)
     * @return the updated bill DTO
     */
    BillDTO applyDiscount(String billId, BigDecimal discountPercent);

    /**
     * Generates a PDF representation of the bill.
     *
     * @param billId the bill UUID
     * @return PDF content as a byte array
     */
    byte[] generateBillPDF(String billId);

    /**
     * Marks a bill as paid by recording the payment.
     *
     * @param billId     the bill UUID
     * @param paymentDTO the payment details
     */
    void markAsPaid(String billId, PaymentDTO paymentDTO);

    /**
     * Calculates the total bill amount using BigDecimal precision.
     *
     * @param numNights    number of nights
     * @param ratePerNight nightly room rate
     * @param taxRate      tax rate as a percentage
     * @param discount     discount amount
     * @return the total amount due
     */
    BigDecimal calculateTotal(int numNights, BigDecimal ratePerNight,
                               BigDecimal taxRate, BigDecimal discount);

    /**
     * Returns all unpaid bills.
     *
     * @return list of unpaid bill DTOs
     */
    List<BillDTO> getUnpaidBills();

    /**
     * Returns all bills.
     *
     * @return list of all bill DTOs
     */
    List<BillDTO> getAllBills();
}
