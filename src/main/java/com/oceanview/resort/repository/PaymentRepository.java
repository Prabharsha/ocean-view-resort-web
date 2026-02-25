package com.oceanview.resort.repository;

import com.oceanview.resort.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA Repository for {@link Payment} entity.
 * Provides standard CRUD operations and custom query methods for payments.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    /**
     * Finds all payments for a specific bill.
     */
    List<Payment> findByBillId(String billId);

    /**
     * Finds all payments for a specific reservation.
     */
    List<Payment> findByReservationId(String reservationId);

    /**
     * Finds all payments processed by a specific user.
     */
    List<Payment> findByProcessedById(String userId);
}
