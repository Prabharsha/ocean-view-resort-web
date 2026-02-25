package com.oceanview.resort.repository;

import com.oceanview.resort.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for {@link Bill} entity.
 * Provides standard CRUD operations and custom query methods for bills.
 */
@Repository
public interface BillRepository extends JpaRepository<Bill, String> {

    /**
     * Finds a bill by the associated reservation ID.
     */
    Optional<Bill> findByReservationId(String reservationId);

    /**
     * Finds all unpaid bills.
     */
    List<Bill> findByPaymentStatus(String paymentStatus);

    /**
     * Checks if a bill already exists for the given reservation.
     */
    boolean existsByReservationId(String reservationId);
}
