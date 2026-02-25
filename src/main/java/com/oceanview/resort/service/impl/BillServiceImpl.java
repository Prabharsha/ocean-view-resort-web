package com.oceanview.resort.service.impl;

import com.oceanview.resort.dto.BillDTO;
import com.oceanview.resort.dto.PaymentDTO;
import com.oceanview.resort.exception.InvalidPaymentException;
import com.oceanview.resort.exception.ReservationNotFoundException;
import com.oceanview.resort.mapper.BillMapper;
import com.oceanview.resort.model.Bill;
import com.oceanview.resort.model.Customer;
import com.oceanview.resort.model.Payment;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.User;
import com.oceanview.resort.repository.BillRepository;
import com.oceanview.resort.repository.PaymentRepository;
import com.oceanview.resort.repository.ReservationRepository;
import com.oceanview.resort.repository.UserRepository;
import com.oceanview.resort.service.EmailService;
import com.oceanview.resort.service.interfaces.BillService;
import com.oceanview.resort.service.interfaces.LoyaltyService;
import com.oceanview.resort.util.BillCalculator;
import com.oceanview.resort.util.PdfGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link BillService}.
 *
 * <p>Handles bill generation, tax/discount calculation, and payment status updates.
 * All monetary calculations use {@link BigDecimal} for precision via
 * {@link BillCalculator}.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final BillMapper billMapper;
    private final BillCalculator billCalculator;
    private final PdfGenerator pdfGenerator;
    private final LoyaltyService loyaltyService;
    private final EmailService emailService;

    /** {@inheritDoc} */
    @Override
    @Transactional
    public BillDTO generateBill(String reservationId) {
        log.info("Generating bill for reservation: {}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("id", reservationId));

        // Check if bill already exists
        if (billRepository.existsByReservationId(reservationId)) {
            throw new IllegalStateException("Bill already exists for reservation: " + reservationId);
        }

        // Calculate bill amounts
        int numNights = (int) ChronoUnit.DAYS.between(
                reservation.getCheckInDate(), reservation.getCheckOutDate());
        if (numNights < 1) numNights = 1;

        BigDecimal roomRate = reservation.getRoom().getRatePerNight();
        BigDecimal taxRate = new BigDecimal("10.00");
        BigDecimal discount = BigDecimal.ZERO;

        BigDecimal subtotal = billCalculator.calculateSubtotal(numNights, roomRate);
        BigDecimal taxAmount = billCalculator.calculateTax(subtotal, taxRate);
        BigDecimal totalAmount = billCalculator.calculateTotal(numNights, roomRate, taxRate, discount);

        Bill bill = Bill.builder()
                .reservation(reservation)
                .numNights(numNights)
                .roomRate(roomRate)
                .subtotal(subtotal)
                .taxRate(taxRate)
                .taxAmount(taxAmount)
                .discountAmount(discount)
                .totalAmount(totalAmount)
                .paymentStatus("UNPAID")
                .build();

        Bill saved = billRepository.save(bill);
        log.info("Bill generated for reservation {}: total = {}", reservationId, totalAmount);
        return billMapper.toDTO(saved);
    }

    /** {@inheritDoc} */
    @Override
    public BillDTO findBillByReservationId(String reservationId) {
        log.debug("Finding bill for reservation: {}", reservationId);
        Bill bill = billRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No bill found for reservation: " + reservationId));
        return billMapper.toDTO(bill);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public BillDTO applyDiscount(String billId, BigDecimal discountPercent) {
        log.info("Applying {}% discount to bill: {}", discountPercent, billId);

        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found: " + billId));

        // Calculate discount amount from percentage
        BigDecimal discountAmount = bill.getSubtotal()
                .multiply(discountPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        bill.setDiscountAmount(discountAmount);

        // Recalculate total
        BigDecimal totalAmount = bill.getSubtotal()
                .add(bill.getTaxAmount())
                .subtract(discountAmount)
                .setScale(2, RoundingMode.HALF_UP);
        bill.setTotalAmount(totalAmount);

        Bill saved = billRepository.save(bill);
        log.info("Discount applied. New total: {}", totalAmount);
        return billMapper.toDTO(saved);
    }

    /** {@inheritDoc} */
    @Override
    public byte[] generateBillPDF(String billId) {
        log.info("Generating PDF for bill: {}", billId);
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found: " + billId));

        return pdfGenerator.generateBillPdf(
                bill.getReservation().getReservationNumber(),
                bill.getReservation().getGuestName(),
                bill.getNumNights(),
                bill.getRoomRate(),
                bill.getSubtotal(),
                bill.getTaxRate(),
                bill.getTaxAmount(),
                bill.getDiscountAmount(),
                bill.getTotalAmount(),
                bill.getPaymentStatus()
        );
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void markAsPaid(String billId, PaymentDTO paymentDTO) {
        log.info("Processing payment for bill: {}", billId);

        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found: " + billId));

        if ("PAID".equals(bill.getPaymentStatus())) {
            throw new InvalidPaymentException("Bill is already fully paid");
        }

        // Create and save payment
        Payment payment = Payment.builder()
                .bill(bill)
                .reservation(bill.getReservation())
                .amountPaid(paymentDTO.getAmountPaid())
                .paymentMethod(paymentDTO.getPaymentMethod())
                .transactionReference(paymentDTO.getTransactionReference())
                .notes(paymentDTO.getNotes())
                .build();

        // Set processed by staff if provided
        if (paymentDTO.getProcessedBy() != null) {
            User staff = userRepository.findById(paymentDTO.getProcessedBy())
                    .orElse(null);
            payment.setProcessedBy(staff);
        }

        paymentRepository.save(payment);

        // Update bill payment status
        BigDecimal totalPaid = paymentRepository.findByBillId(billId).stream()
                .map(Payment::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPaid.compareTo(bill.getTotalAmount()) >= 0) {
            bill.setPaymentStatus("PAID");

            // Award loyalty points: 1 point per £1 paid
            try {
                Reservation reservation = bill.getReservation();
                if (reservation != null && reservation.getCustomer() != null) {
                    Customer customer = reservation.getCustomer();
                    int pointsEarned = bill.getTotalAmount().intValue();
                    if (pointsEarned > 0) {
                        loyaltyService.earnPoints(customer.getId(), pointsEarned,
                                "Earned from reservation " + reservation.getReservationNumber(),
                                reservation.getId());
                        log.info("Awarded {} loyalty points to customer {}",
                                pointsEarned, customer.getId());
                    }

                    // Send bill receipt email
                    emailService.sendBillReceipt(
                            customer.getEmail(),
                            customer.getFullName(),
                            billId,
                            bill.getTotalAmount().toPlainString());
                }
            } catch (Exception e) {
                log.warn("Failed to award loyalty points or send receipt: {}", e.getMessage());
            }
        } else {
            bill.setPaymentStatus("PARTIALLY_PAID");
        }

        billRepository.save(bill);
        log.info("Payment of {} recorded for bill {}. Status: {}",
                paymentDTO.getAmountPaid(), billId, bill.getPaymentStatus());
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal calculateTotal(int numNights, BigDecimal ratePerNight,
                                     BigDecimal taxRate, BigDecimal discount) {
        return billCalculator.calculateTotal(numNights, ratePerNight, taxRate, discount);
    }

    /** {@inheritDoc} */
    @Override
    public List<BillDTO> getUnpaidBills() {
        log.debug("Finding all unpaid bills");
        return billRepository.findByPaymentStatus("UNPAID").stream()
                .map(billMapper::toDTO)
                .collect(Collectors.toList());
    }
}
