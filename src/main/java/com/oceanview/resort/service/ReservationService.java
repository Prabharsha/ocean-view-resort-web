package com.oceanview.resort.service;

import com.oceanview.resort.dao.*;
import com.oceanview.resort.model.Customer;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.enums.ReservationStatus;
import com.oceanview.resort.model.enums.RoomType;
import com.oceanview.resort.util.ReservationNumberGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

/**
 * Service Layer for reservation management operations.
 *
 * <p><b>Design Pattern: Service Layer Pattern</b></p>
 */
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationDAO reservationDAO;
    private final RoomDAO roomDAO;
    private final UserDAO userDAO;
    private final CustomerDAO customerDAO;
    private final ReservationNumberGenerator resNumGen;

    public ReservationService(ReservationDAO reservationDAO, RoomDAO roomDAO,
                              UserDAO userDAO, CustomerDAO customerDAO,
                              ReservationNumberGenerator resNumGen) {
        this.reservationDAO = reservationDAO;
        this.roomDAO = roomDAO;
        this.userDAO = userDAO;
        this.customerDAO = customerDAO;
        this.resNumGen = resNumGen;
    }

    /** Creates a new reservation after validating room availability. */
    public Reservation createReservation(Reservation reservation) {
        log.info("Creating reservation for customer: {}", reservation.getCustomerId());

        // Validate dates
        if (reservation.getCheckOutDate() == null || reservation.getCheckInDate() == null ||
            !reservation.getCheckOutDate().isAfter(reservation.getCheckInDate())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        // Validate room availability
        if (reservationDAO.existsOverlappingReservation(
                reservation.getRoomId(), reservation.getCheckInDate(), reservation.getCheckOutDate())) {
            throw new IllegalStateException("Room is not available for the selected dates");
        }

        // Auto-populate guest fields from customer if not provided
        if (reservation.getGuestName() == null || reservation.getGuestName().isBlank()) {
            customerDAO.findById(reservation.getCustomerId()).ifPresent(c ->
                    reservation.setGuestName(c.getFullName()));
        }
        if (reservation.getGuestContact() == null || reservation.getGuestContact().isBlank()) {
            customerDAO.findById(reservation.getCustomerId()).ifPresent(c ->
                    reservation.setGuestContact(c.getPhone() != null ? c.getPhone() : "N/A"));
        }

        reservation.setReservationNumber(resNumGen.generateNext());
        reservation.setStatus(ReservationStatus.PENDING);

        String id = reservationDAO.save(reservation);
        reservation.setId(id);

        // Enrich with room/customer info for response
        roomDAO.findById(reservation.getRoomId()).ifPresent(room -> {
            reservation.setRoomNumber(room.getRoomNumber());
            reservation.setRoomType(room.getRoomType().name());
        });

        log.info("Reservation created: {}", reservation.getReservationNumber());
        return reservation;
    }

    public Reservation findById(String id) {
        return reservationDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + id));
    }

    public Reservation findByReservationNumber(String number) {
        return reservationDAO.findByReservationNumber(number)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + number));
    }

    public List<Reservation> findAll() {
        return reservationDAO.findAll();
    }

    public List<Reservation> findByCustomerId(String customerId) {
        return reservationDAO.findByCustomerId(customerId);
    }

    public List<Reservation> findByDateRange(LocalDate start, LocalDate end) {
        return reservationDAO.findByCheckInDateBetween(start, end);
    }

    public Reservation confirmReservation(String reservationNumber) {
        Reservation r = findByReservationNumber(reservationNumber);
        if (r.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Only PENDING reservations can be confirmed");
        }
        reservationDAO.updateStatus(r.getId(), ReservationStatus.CONFIRMED);
        r.setStatus(ReservationStatus.CONFIRMED);
        return r;
    }

    public void cancelReservation(String id) {
        Reservation r = findById(id);
        if (r.getStatus() == ReservationStatus.CHECKED_IN || r.getStatus() == ReservationStatus.CHECKED_OUT) {
            throw new IllegalStateException("Cannot cancel: reservation is " + r.getStatus());
        }
        reservationDAO.updateStatus(id, ReservationStatus.CANCELLED);
    }

    public Reservation checkIn(String reservationNumber) {
        Reservation r = findByReservationNumber(reservationNumber);
        if (r.getStatus() != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("Only CONFIRMED reservations can be checked in");
        }
        reservationDAO.updateStatus(r.getId(), ReservationStatus.CHECKED_IN);
        r.setStatus(ReservationStatus.CHECKED_IN);
        return r;
    }

    public Reservation checkOut(String reservationNumber) {
        Reservation r = findByReservationNumber(reservationNumber);
        if (r.getStatus() != ReservationStatus.CHECKED_IN) {
            throw new IllegalStateException("Only CHECKED_IN reservations can be checked out");
        }
        reservationDAO.updateStatus(r.getId(), ReservationStatus.CHECKED_OUT);
        r.setStatus(ReservationStatus.CHECKED_OUT);
        return r;
    }

    public Reservation updateStatus(String id, ReservationStatus status) {
        Reservation r = findById(id);
        reservationDAO.updateStatus(id, status);
        r.setStatus(status);
        return r;
    }

    public boolean isRoomAvailable(String roomId, LocalDate checkIn, LocalDate checkOut) {
        return !reservationDAO.existsOverlappingReservation(roomId, checkIn, checkOut);
    }

    public List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, RoomType type) {
        return roomDAO.findAvailableRooms(checkIn, checkOut, type);
    }
}

