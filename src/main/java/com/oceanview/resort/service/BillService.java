package com.oceanview.resort.service;

import com.oceanview.resort.dao.*;
import com.oceanview.resort.model.*;
import com.oceanview.resort.model.enums.PaymentMethod;
import com.oceanview.resort.util.BillCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service Layer for bill management.
 */
public class BillService {

    private static final Logger log = LoggerFactory.getLogger(BillService.class);

    private final BillDAO billDAO;
    private final ReservationDAO reservationDAO;
    private final PaymentDAO paymentDAO;
    private final RoomDAO roomDAO;
    private final BillCalculator billCalculator;

    public BillService(BillDAO billDAO, ReservationDAO reservationDAO, PaymentDAO paymentDAO,
                       RoomDAO roomDAO, BillCalculator billCalculator) {
        this.billDAO = billDAO;
        this.reservationDAO = reservationDAO;
        this.paymentDAO = paymentDAO;
        this.roomDAO = roomDAO;
        this.billCalculator = billCalculator;
    }

    /** Generates a bill or returns existing one. */
    public Bill generateOrGetBill(String reservationId) {
        if (billDAO.existsByReservationId(reservationId)) {
            return findByReservationId(reservationId);
        }
        return generateBill(reservationId);
    }

    public Bill generateBill(String reservationId) {
        log.info("Generating bill for reservation: {}", reservationId);

        Reservation reservation = reservationDAO.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));

        if (billDAO.existsByReservationId(reservationId)) {
            throw new IllegalStateException("Bill already exists for reservation: " + reservationId);
        }

        Room room = roomDAO.findById(reservation.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + reservation.getRoomId()));

        int numNights = (int) ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate());
        if (numNights < 1) numNights = 1;

        BigDecimal roomRate = room.getRatePerNight();
        BigDecimal taxRate = new BigDecimal("10.00");
        BigDecimal discount = BigDecimal.ZERO;

        BigDecimal subtotal = billCalculator.calculateSubtotal(numNights, roomRate);
        BigDecimal taxAmount = billCalculator.calculateTax(subtotal, taxRate);
        BigDecimal totalAmount = billCalculator.calculateTotal(numNights, roomRate, taxRate, discount);

        Bill bill = new Bill();
        bill.setReservationId(reservationId);
        bill.setNumNights(numNights);
        bill.setRoomRate(roomRate);
        bill.setSubtotal(subtotal);
        bill.setTaxRate(taxRate);
        bill.setTaxAmount(taxAmount);
        bill.setDiscountAmount(discount);
        bill.setTotalAmount(totalAmount);
        bill.setPaymentStatus("UNPAID");

        String id = billDAO.save(bill);
        bill.setId(id);

        // Enrich with display fields
        bill.setReservationNumber(reservation.getReservationNumber());
        bill.setGuestName(reservation.getGuestName());
        bill.setRoomNumber(room.getRoomNumber());
        bill.setRoomType(room.getRoomType().name());

        log.info("Bill generated: total = {}", totalAmount);
        return bill;
    }

    public Bill findById(String billId) {
        return billDAO.findById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found: " + billId));
    }

    public Bill findByReservationId(String reservationId) {
        return billDAO.findByReservationId(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("No bill for reservation: " + reservationId));
    }

    public List<Bill> findAll() { return billDAO.findAll(); }

    public List<Bill> findUnpaid() { return billDAO.findByPaymentStatus("UNPAID"); }

    public Bill applyDiscount(String billId, BigDecimal discountPercent) {
        Bill bill = findById(billId);
        BigDecimal discountAmount = bill.getSubtotal()
                .multiply(discountPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        bill.setDiscountAmount(discountAmount);
        BigDecimal total = bill.getSubtotal().add(bill.getTaxAmount()).subtract(discountAmount)
                .setScale(2, RoundingMode.HALF_UP);
        bill.setTotalAmount(total);
        billDAO.update(bill);
        return bill;
    }

    public void markAsPaid(String billId, BigDecimal amountPaid, PaymentMethod method,
                           String transactionRef, String notes, String processedBy) {
        Bill bill = findById(billId);
        if ("PAID".equals(bill.getPaymentStatus())) {
            throw new IllegalStateException("Bill is already fully paid");
        }

        Reservation reservation = reservationDAO.findById(bill.getReservationId())
                .orElse(null);

        Payment payment = new Payment();
        payment.setBillId(billId);
        payment.setReservationId(bill.getReservationId());
        payment.setAmountPaid(amountPaid);
        payment.setPaymentMethod(method);
        payment.setTransactionReference(transactionRef);
        payment.setNotes(notes);
        payment.setProcessedBy(processedBy);
        paymentDAO.save(payment);

        BigDecimal totalPaid = paymentDAO.getTotalPaidForBill(billId);
        if (totalPaid.compareTo(bill.getTotalAmount()) >= 0) {
            bill.setPaymentStatus("PAID");
        } else {
            bill.setPaymentStatus("PARTIALLY_PAID");
        }
        billDAO.update(bill);
        log.info("Payment of {} recorded for bill {}. Status: {}", amountPaid, billId, bill.getPaymentStatus());
    }

    public List<Payment> getPaymentsForBill(String billId) {
        return paymentDAO.findByBillId(billId);
    }
}

