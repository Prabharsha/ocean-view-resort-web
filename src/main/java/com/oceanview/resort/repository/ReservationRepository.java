package com.oceanview.resort.repository;

import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for {@link Reservation} entity.
 *
 * <p><b>Design Pattern: Repository Pattern</b></p>
 * <p>The Repository Pattern mediates between the domain and data mapping layers
 * using a collection-like interface for accessing domain objects. Spring Data JPA
 * implements this pattern by extending {@link JpaRepository}, which provides:</p>
 * <ul>
 *   <li>Standard CRUD operations (save, findById, findAll, delete).</li>
 *   <li>Pagination and sorting support.</li>
 *   <li>Custom query methods via method naming conventions (e.g., {@code findByCustomerId}).</li>
 *   <li>Custom JPQL queries via {@code @Query} annotations.</li>
 * </ul>
 *
 * <h3>Benefits:</h3>
 * <ul>
 *   <li>Abstracts data access logic from the service layer.</li>
 *   <li>Spring auto-generates the implementation at runtime — no boilerplate code.</li>
 *   <li>Type-safe queries via method signatures.</li>
 *   <li>Easy to test by mocking the repository interface.</li>
 * </ul>
 *
 * @see com.oceanview.resort.dao.interfaces.ReservationDAO
 * @see com.oceanview.resort.service.impl.ReservationServiceImpl
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {

    /**
     * Finds a reservation by its unique reservation number.
     */
    Optional<Reservation> findByReservationNumber(String reservationNumber);

    /**
     * Finds all reservations for a specific customer.
     */
    List<Reservation> findByCustomerId(String customerId);

    /**
     * Finds reservations whose check-in date falls within the given range.
     */
    List<Reservation> findByCheckInDateBetween(LocalDate start, LocalDate end);

    /**
     * Finds reservations by status.
     */
    List<Reservation> findByStatus(ReservationStatus status);

    /**
     * Checks whether a room has any overlapping reservations for the given dates.
     * A reservation overlaps if its check-in is before the requested check-out
     * AND its check-out is after the requested check-in, and it is not cancelled.
     */
    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
           "WHERE r.room.id = :roomId " +
           "AND r.status <> com.oceanview.resort.model.enums.ReservationStatus.CANCELLED " +
           "AND r.checkInDate < :checkOut " +
           "AND r.checkOutDate > :checkIn")
    boolean existsOverlappingReservation(@Param("roomId") String roomId,
                                         @Param("checkIn") LocalDate checkIn,
                                         @Param("checkOut") LocalDate checkOut);

    /**
     * Counts reservations created in a given month/year.
     */
    @Query("SELECT COUNT(r) FROM Reservation r " +
           "WHERE YEAR(r.createdAt) = :year AND MONTH(r.createdAt) = :month")
    long countByMonth(@Param("year") int year, @Param("month") int month);

    /**
     * Finds reservations created in a given month/year.
     */
    @Query("SELECT r FROM Reservation r " +
           "WHERE YEAR(r.createdAt) = :year AND MONTH(r.createdAt) = :month")
    List<Reservation> findByMonth(@Param("year") int year, @Param("month") int month);

    /**
     * Finds reservations whose check-in or check-out date falls within the given week range.
     */
    @Query("SELECT r FROM Reservation r " +
           "WHERE r.checkInDate BETWEEN :weekStart AND :weekEnd " +
           "OR r.checkOutDate BETWEEN :weekStart AND :weekEnd")
    List<Reservation> findByWeekRange(@Param("weekStart") LocalDate weekStart,
                                      @Param("weekEnd") LocalDate weekEnd);

    /**
     * Counts the latest reservation number sequence for the current year.
     */
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.reservationNumber LIKE CONCAT('OVR-', :year, '-%')")
    long countByYear(@Param("year") int year);
}
