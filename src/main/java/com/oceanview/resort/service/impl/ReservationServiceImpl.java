package com.oceanview.resort.service.impl;

import com.oceanview.resort.dto.ReservationDTO;
import com.oceanview.resort.event.CheckInEvent;
import com.oceanview.resort.event.CheckOutEvent;
import com.oceanview.resort.event.ReservationCreatedEvent;
import com.oceanview.resort.exception.ReservationNotFoundException;
import com.oceanview.resort.exception.RoomNotAvailableException;
import com.oceanview.resort.mapper.ReservationMapper;
import com.oceanview.resort.model.Customer;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.User;
import com.oceanview.resort.model.enums.ReservationStatus;
import com.oceanview.resort.model.enums.RoomType;
import com.oceanview.resort.repository.ReservationRepository;
import com.oceanview.resort.repository.RoomRepository;
import com.oceanview.resort.repository.UserRepository;
import com.oceanview.resort.service.interfaces.ReservationService;
import com.oceanview.resort.util.PdfGenerator;
import com.oceanview.resort.util.ReservationNumberGenerator;
import com.oceanview.resort.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link ReservationService}.
 *
 * <p><b>Design Patterns Used:</b></p>
 * <ul>
 *   <li><b>Service Layer Pattern:</b> Contains all business logic for reservation
 *       management, isolated from controllers (presentation) and repositories (data).
 *       Implements the {@link ReservationService} interface for loose coupling.</li>
 *   <li><b>Observer Pattern:</b> Uses Spring's {@link ApplicationEventPublisher} to
 *       publish lifecycle events ({@link ReservationCreatedEvent}, {@link CheckInEvent},
 *       {@link CheckOutEvent}). Listeners ({@code EmailNotificationListener},
 *       {@code AuditLogListener}) subscribe to these events without the service
 *       needing to know about them — achieving loose coupling.</li>
 *   <li><b>Singleton Pattern:</b> The {@code @Service} annotation makes this a
 *       Spring-managed singleton bean. Only one instance exists in the application
 *       context throughout the application lifecycle.</li>
 * </ul>
 *
 * <p>Contains all business logic for reservation management including
 * creation, validation, check-in/check-out, and notification dispatching.
 * Uses {@code @Transactional} annotations for data consistency.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ReservationMapper reservationMapper;
    private final ReservationNumberGenerator reservationNumberGenerator;
    private final PdfGenerator pdfGenerator;

    /**
     * Observer Pattern: Spring's event publisher for broadcasting reservation
     * lifecycle events to all registered listeners (EmailNotificationListener,
     * AuditLogListener, etc.).
     */
    private final ApplicationEventPublisher eventPublisher;

    /**
     * {@inheritDoc}
     *
     * <p>Validates date range and room availability, generates a unique
     * reservation number, and persists the reservation.</p>
     */
    @Override
    @Transactional
    public ReservationDTO createReservation(ReservationDTO dto) {
        log.info("Creating reservation for customer: {}", dto.getCustomerId());

        // Validate date range
        if (!ValidationUtil.isValidDateRange(dto.getCheckInDate(), dto.getCheckOutDate())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        // Validate room availability
        if (!isRoomAvailable(dto.getRoomId(), dto.getCheckInDate(), dto.getCheckOutDate())) {
            throw new RoomNotAvailableException(dto.getRoomId(),
                    dto.getCheckInDate() + " to " + dto.getCheckOutDate());
        }

        // Resolve entities
        Customer customer = (Customer) userRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + dto.getCustomerId()));
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + dto.getRoomId()));

        // Build reservation entity
        Reservation reservation = reservationMapper.toEntity(dto);
        reservation.setCustomer(customer);
        reservation.setRoom(room);
        reservation.setReservationNumber(reservationNumberGenerator.generateNext());
        reservation.setStatus(ReservationStatus.PENDING);

        // Optionally set staff
        if (dto.getStaffId() != null) {
            User staff = userRepository.findById(dto.getStaffId())
                    .orElseThrow(() -> new IllegalArgumentException("Staff not found: " + dto.getStaffId()));
            reservation.setStaff(staff);
        }

        Reservation saved = reservationRepository.save(reservation);
        log.info("Reservation created: {}", saved.getReservationNumber());

        ReservationDTO resultDTO = reservationMapper.toDTO(saved);

        // Observer Pattern: publish ReservationCreatedEvent to notify all listeners
        eventPublisher.publishEvent(new ReservationCreatedEvent(this, resultDTO));
        log.debug("ReservationCreatedEvent published for: {}", saved.getReservationNumber());

        return resultDTO;
    }

    /** {@inheritDoc} */
    @Override
    public ReservationDTO findByReservationNumber(String reservationNumber) {
        log.debug("Finding reservation by number: {}", reservationNumber);
        Reservation reservation = reservationRepository.findByReservationNumber(reservationNumber)
                .orElseThrow(() -> new ReservationNotFoundException("reservationNumber", reservationNumber));
        return reservationMapper.toDTO(reservation);
    }

    /** {@inheritDoc} */
    @Override
    public List<ReservationDTO> findAllReservations() {
        log.debug("Finding all reservations");
        return reservationRepository.findAll().stream()
                .map(reservationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    public List<ReservationDTO> findByCustomerId(String customerId) {
        log.debug("Finding reservations for customer: {}", customerId);
        return reservationRepository.findByCustomerId(customerId).stream()
                .map(reservationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    public List<ReservationDTO> findByDateRange(LocalDate start, LocalDate end) {
        log.debug("Finding reservations between {} and {}", start, end);
        return reservationRepository.findByCheckInDateBetween(start, end).stream()
                .map(reservationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public ReservationDTO updateReservationStatus(String id, ReservationStatus status) {
        log.info("Updating reservation {} status to {}", id, status);
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("id", id));
        reservation.setStatus(status);
        Reservation saved = reservationRepository.save(reservation);
        return reservationMapper.toDTO(saved);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void cancelReservation(String id) {
        log.info("Cancelling reservation: {}", id);
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("id", id));

        if (reservation.getStatus() == ReservationStatus.CHECKED_IN ||
            reservation.getStatus() == ReservationStatus.CHECKED_OUT) {
            throw new IllegalStateException(
                    "Cannot cancel a reservation that is already " + reservation.getStatus().getDisplayName());
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        log.info("Reservation {} cancelled", reservation.getReservationNumber());
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRoomAvailable(String roomId, LocalDate checkIn, LocalDate checkOut) {
        return !reservationRepository.existsOverlappingReservation(roomId, checkIn, checkOut);
    }

    /** {@inheritDoc} */
    @Override
    public List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, RoomType type) {
        log.debug("Finding available rooms from {} to {} (type={})", checkIn, checkOut, type);
        return roomRepository.findAvailableRooms(checkIn, checkOut, type);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public ReservationDTO checkIn(String reservationNumber) {
        log.info("Checking in reservation: {}", reservationNumber);
        Reservation reservation = reservationRepository.findByReservationNumber(reservationNumber)
                .orElseThrow(() -> new ReservationNotFoundException("reservationNumber", reservationNumber));

        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Only CONFIRMED reservations can be checked in. Current status: " +
                    reservation.getStatus().getDisplayName());
        }

        reservation.setStatus(ReservationStatus.CHECKED_IN);
        Reservation saved = reservationRepository.save(reservation);
        log.info("Reservation {} checked in", reservationNumber);

        ReservationDTO resultDTO = reservationMapper.toDTO(saved);

        // Observer Pattern: publish CheckInEvent to notify all listeners
        eventPublisher.publishEvent(new CheckInEvent(this, resultDTO));
        log.debug("CheckInEvent published for: {}", reservationNumber);

        return resultDTO;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public ReservationDTO checkOut(String reservationNumber) {
        log.info("Checking out reservation: {}", reservationNumber);
        Reservation reservation = reservationRepository.findByReservationNumber(reservationNumber)
                .orElseThrow(() -> new ReservationNotFoundException("reservationNumber", reservationNumber));

        if (reservation.getStatus() != ReservationStatus.CHECKED_IN) {
            throw new IllegalStateException(
                    "Only CHECKED_IN reservations can be checked out. Current status: " +
                    reservation.getStatus().getDisplayName());
        }

        reservation.setStatus(ReservationStatus.CHECKED_OUT);
        Reservation saved = reservationRepository.save(reservation);
        log.info("Reservation {} checked out", reservationNumber);

        ReservationDTO resultDTO = reservationMapper.toDTO(saved);

        // Observer Pattern: publish CheckOutEvent to notify all listeners
        eventPublisher.publishEvent(new CheckOutEvent(this, resultDTO));
        log.debug("CheckOutEvent published for: {}", reservationNumber);

        return resultDTO;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Generates a simple PDF confirmation. Full JasperReports integration
     * is handled in a later task — this provides a basic placeholder.</p>
     */
    @Override
    public byte[] generateReservationConfirmationPDF(String reservationId) {
        log.info("Generating confirmation PDF for reservation: {}", reservationId);
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("id", reservationId));

        return pdfGenerator.generateReservationConfirmation(
                reservation.getReservationNumber(),
                reservation.getGuestName(),
                reservation.getRoom().getRoomNumber(),
                reservation.getRoom().getRoomType().getDisplayName(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getNumGuests()
        );
    }
}
