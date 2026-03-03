package com.oceanview.resort.service.interfaces;

import com.oceanview.resort.dto.ReservationDTO;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.enums.ReservationStatus;
import com.oceanview.resort.model.enums.RoomType;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for reservation management operations.
 *
 * <p><b>Design Pattern: Service Layer Pattern</b></p>
 * <p>The Service Layer Pattern defines an application's boundary with a layer
 * of services that establishes a set of available operations. This interface
 * separates the service contract from its implementation:</p>
 * <ul>
 *   <li><b>Interface:</b> {@code ReservationService} — defines the API.</li>
 *   <li><b>Implementation:</b> {@code ReservationServiceImpl} — contains business logic.</li>
 *   <li><b>Decorator:</b> {@code LoggingReservationService} — adds cross-cutting concerns.</li>
 * </ul>
 *
 * <h3>Benefits:</h3>
 * <ul>
 *   <li>Loose coupling: controllers depend on the interface, not the implementation.</li>
 *   <li>Testability: easily mockable in unit tests.</li>
 *   <li>Multiple implementations: decorator wrapping, proxying, etc.</li>
 *   <li>Transaction boundaries: {@code @Transactional} on the implementation.</li>
 * </ul>
 *
 * <p>Provides methods for creating, querying, updating, cancelling,
 * checking in/out reservations, and generating confirmation PDFs.</p>
 */
public interface ReservationService {

    /**
     * Creates a new reservation after validating room availability.
     *
     * @param dto the reservation data
     * @return the created reservation DTO with generated reservation number
     */
    ReservationDTO createReservation(ReservationDTO dto);

    /**
     * Finds a reservation by its unique reservation number.
     *
     * @param reservationNumber the reservation number (e.g. OVR-2026-000001)
     * @return the reservation DTO
     */
    ReservationDTO findByReservationNumber(String reservationNumber);

    /**
     * Finds a reservation by its UUID.
     *
     * @param id the reservation UUID
     * @return the reservation DTO (including billId if a bill exists)
     */
    ReservationDTO findById(String id);

    /**
     * Returns all reservations in the system.
     *
     * @return list of all reservation DTOs
     */
    List<ReservationDTO> findAllReservations();

    /**
     * Finds all reservations for a specific customer.
     *
     * @param customerId the customer's UUID
     * @return list of reservation DTOs
     */
    List<ReservationDTO> findByCustomerId(String customerId);

    /**
     * Finds reservations whose check-in date falls within the given range.
     *
     * @param start range start date (inclusive)
     * @param end   range end date (inclusive)
     * @return list of matching reservation DTOs
     */
    List<ReservationDTO> findByDateRange(LocalDate start, LocalDate end);

    /**
     * Updates the status of a reservation.
     *
     * @param id     the reservation UUID
     * @param status the new status
     * @return the updated reservation DTO
     */
    ReservationDTO updateReservationStatus(String id, ReservationStatus status);

    /**
     * Confirms a reservation by its reservation number (PENDING → CONFIRMED).
     *
     * @param reservationNumber the reservation number (e.g. OVR-2026-000001)
     * @return the updated reservation DTO
     */
    ReservationDTO confirmReservation(String reservationNumber);

    /**
     * Cancels a reservation.
     *
     * @param id the reservation UUID
     */
    void cancelReservation(String id);

    /**
     * Checks whether a room is available for the given date range.
     *
     * @param roomId   the room UUID
     * @param checkIn  desired check-in date
     * @param checkOut desired check-out date
     * @return {@code true} if the room is available
     */
    boolean isRoomAvailable(String roomId, LocalDate checkIn, LocalDate checkOut);

    /**
     * Finds rooms available for the given date range and optional room type.
     *
     * @param checkIn  desired check-in date
     * @param checkOut desired check-out date
     * @param type     optional room type filter (may be {@code null})
     * @return list of available rooms
     */
    List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, RoomType type);

    /**
     * Marks a reservation as checked-in.
     *
     * @param reservationNumber the reservation number
     * @return the updated reservation DTO
     */
    ReservationDTO checkIn(String reservationNumber);

    /**
     * Marks a reservation as checked-out.
     *
     * @param reservationNumber the reservation number
     * @return the updated reservation DTO
     */
    ReservationDTO checkOut(String reservationNumber);

    /**
     * Generates a PDF confirmation for a reservation.
     *
     * @param reservationId the reservation UUID
     * @return PDF content as a byte array
     */
    byte[] generateReservationConfirmationPDF(String reservationId);
}
