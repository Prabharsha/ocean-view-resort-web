package com.oceanview.resort.service.impl;

import com.oceanview.resort.dto.PaymentDTO;
import com.oceanview.resort.exception.InvalidPaymentException;
import com.oceanview.resort.model.Bill;
import com.oceanview.resort.model.Payment;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.User;
import com.oceanview.resort.repository.BillRepository;
import com.oceanview.resort.repository.PaymentRepository;
import com.oceanview.resort.repository.ReservationRepository;
import com.oceanview.resort.repository.UserRepository;
import com.oceanview.resort.service.factory.PaymentStrategyFactory;
import com.oceanview.resort.service.interfaces.PaymentService;
import com.oceanview.resort.service.strategy.PaymentRequest;
import com.oceanview.resort.service.strategy.PaymentResult;
import com.oceanview.resort.service.strategy.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link PaymentService}.
 *
 * <p><b>Design Patterns Used:</b></p>
 * <ul>
 *   <li><b>Service Layer Pattern:</b> Encapsulates business logic for payment
 *       processing, isolating it from controllers and data access layers.</li>
 *   <li><b>Strategy Pattern:</b> Delegates payment processing to the appropriate
 *       {@link PaymentStrategy} implementation selected via the
 *       {@link PaymentStrategyFactory} (Factory Pattern). Each payment method
 *       (Cash, Card, Points, Online) has its own strategy class.</li>
 *   <li><b>Singleton Pattern:</b> The {@code @Service} annotation makes this a
 *       Spring-managed singleton bean — only one instance exists in the
 *       application context.</li>
 * </ul>
 *
 * <p>Handles payment processing, validation, and payment history queries.
 * Updates the associated {@link Bill} payment status upon each transaction.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BillRepository billRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    /** Factory Pattern: creates the appropriate payment strategy based on method type. */
    private final PaymentStrategyFactory paymentStrategyFactory;

    /**
     * {@inheritDoc}
     *
     * <p><b>Strategy Pattern in action:</b> This method uses the
     * {@link PaymentStrategyFactory} to obtain the correct {@link PaymentStrategy}
     * for the given payment method, then delegates the processing to that strategy.
     * The result is used to set the transaction reference on the persisted payment.</p>
     *
     * <h3>Processing flow:</h3>
     * <ol>
     *   <li>Validate the bill exists and is not already fully paid.</li>
     *   <li>Use Factory Pattern to get the appropriate Strategy.</li>
     *   <li>Execute the Strategy to process the payment.</li>
     *   <li>Persist the payment entity with the strategy result.</li>
     *   <li>Update the bill's payment status (PAID / PARTIALLY_PAID).</li>
     * </ol>
     */
    @Override
    @Transactional
    public PaymentDTO processPayment(PaymentDTO paymentDTO) {
        log.info("Processing payment for bill: {}", paymentDTO.getBillId());

        Bill bill = billRepository.findById(paymentDTO.getBillId())
                .orElseThrow(() -> new InvalidPaymentException(
                        "Bill not found: " + paymentDTO.getBillId()));

        if ("PAID".equals(bill.getPaymentStatus())) {
            throw new InvalidPaymentException("Bill is already fully paid");
        }

        Reservation reservation = bill.getReservation();

        // ── Strategy Pattern: select and execute the appropriate payment strategy ──
        PaymentStrategy strategy = paymentStrategyFactory.getStrategy(paymentDTO.getPaymentMethod());
        log.info("Selected payment strategy: {}", strategy.getClass().getSimpleName());

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .billId(paymentDTO.getBillId())
                .reservationId(reservation.getId())
                .amount(paymentDTO.getAmountPaid())
                .paymentMethod(paymentDTO.getPaymentMethod())
                .transactionReference(paymentDTO.getTransactionReference())
                .notes(paymentDTO.getNotes())
                .processedByStaffId(paymentDTO.getProcessedBy())
                .build();

        PaymentResult result = strategy.execute(paymentRequest);

        if (!result.isSuccess()) {
            throw new InvalidPaymentException("Payment processing failed: " + result.getMessage());
        }
        // ── End Strategy Pattern ──

        Payment payment = Payment.builder()
                .bill(bill)
                .reservation(reservation)
                .amountPaid(paymentDTO.getAmountPaid())
                .paymentMethod(paymentDTO.getPaymentMethod())
                .transactionReference(result.getTransactionReference())
                .notes(paymentDTO.getNotes())
                .build();

        // Set processed by staff if provided
        if (paymentDTO.getProcessedBy() != null) {
            User staff = userRepository.findById(paymentDTO.getProcessedBy())
                    .orElse(null);
            payment.setProcessedBy(staff);
        }

        Payment saved = paymentRepository.save(payment);

        // Update bill payment status
        BigDecimal totalPaid = paymentRepository.findByBillId(bill.getId()).stream()
                .map(Payment::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPaid.compareTo(bill.getTotalAmount()) >= 0) {
            bill.setPaymentStatus("PAID");
        } else {
            bill.setPaymentStatus("PARTIALLY_PAID");
        }
        billRepository.save(bill);

        log.info("Payment processed: {} for bill {}. Status: {}. Strategy: {}",
                saved.getAmountPaid(), bill.getId(), bill.getPaymentStatus(),
                strategy.getClass().getSimpleName());

        return toDTO(saved);
    }

    /** {@inheritDoc} */
    @Override
    public List<PaymentDTO> findByBillId(String billId) {
        log.debug("Finding payments for bill: {}", billId);
        return paymentRepository.findByBillId(billId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    public List<PaymentDTO> findByReservationId(String reservationId) {
        log.debug("Finding payments for reservation: {}", reservationId);
        return paymentRepository.findByReservationId(reservationId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    public PaymentDTO findById(String id) {
        log.debug("Finding payment by id: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + id));
        return toDTO(payment);
    }

    /**
     * Converts a Payment entity to a PaymentDTO.
     */
    private PaymentDTO toDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .billId(payment.getBill() != null ? payment.getBill().getId() : null)
                .reservationId(payment.getReservation() != null
                        ? payment.getReservation().getId() : null)
                .amountPaid(payment.getAmountPaid())
                .paymentMethod(payment.getPaymentMethod())
                .transactionReference(payment.getTransactionReference())
                .paymentDate(payment.getPaymentDate())
                .processedBy(payment.getProcessedBy() != null
                        ? payment.getProcessedBy().getId() : null)
                .notes(payment.getNotes())
                .build();
    }
}
