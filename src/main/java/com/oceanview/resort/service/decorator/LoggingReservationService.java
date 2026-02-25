package com.oceanview.resort.service.decorator;

import com.oceanview.resort.dto.ReservationDTO;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.enums.ReservationStatus;
import com.oceanview.resort.model.enums.RoomType;
import com.oceanview.resort.service.interfaces.ReservationService;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

/**
 * Decorator that adds comprehensive logging to all {@link ReservationService} operations.
 *
 * <p><b>Design Pattern: Decorator Pattern</b></p>
 * <p>The Decorator Pattern attaches additional responsibilities to an object
 * dynamically. Decorators provide a flexible alternative to subclassing for
 * extending functionality. {@code LoggingReservationService} wraps the actual
 * {@link ReservationService} implementation and logs method entry, exit, and
 * execution time for every operation.</p>
 *
 * <h3>How it works in this system:</h3>
 * <ol>
 *   <li><b>Component Interface:</b> {@link ReservationService} defines the contract.</li>
 *   <li><b>Concrete Component:</b> {@code ReservationServiceImpl} provides the real logic.</li>
 *   <li><b>Decorator:</b> {@code LoggingReservationService} wraps the component and adds
 *       logging before and after each method call, including execution timing.</li>
 * </ol>
 *
 * <h3>Benefits:</h3>
 * <ul>
 *   <li><b>Single Responsibility:</b> Logging logic is separated from business logic.</li>
 *   <li><b>Open/Closed Principle:</b> New cross-cutting concerns (e.g., caching,
 *       metrics) can be added as new decorators without modifying existing code.</li>
 *   <li><b>Composability:</b> Multiple decorators can be stacked.</li>
 * </ul>
 *
 * <h3>Configuration:</h3>
 * <p>This decorator is registered as a Spring bean via
 * {@link com.oceanview.resort.config.ServiceDecoratorConfig}, which wraps the
 * primary {@code ReservationServiceImpl} bean with this logging decorator
 * using the {@code @Primary} annotation.</p>
 *
 * @see ReservationService
 * @see com.oceanview.resort.service.impl.ReservationServiceImpl
 */
@Slf4j
public class LoggingReservationService implements ReservationService {

    /** The wrapped (decorated) reservation service implementation. */
    private final ReservationService delegate;

    /**
     * Creates a new logging decorator wrapping the given service.
     *
     * @param delegate the actual ReservationService implementation to decorate
     */
    public LoggingReservationService(ReservationService delegate) {
        this.delegate = delegate;
        log.info("[DECORATOR] LoggingReservationService wrapping: {}", delegate.getClass().getSimpleName());
    }

    /** {@inheritDoc} */
    @Override
    public ReservationDTO createReservation(ReservationDTO dto) {
        log.info("[DECORATOR] >>> createReservation() called. Guest: {}, Room: {}, Check-in: {}, Check-out: {}",
                dto.getGuestName(), dto.getRoomId(), dto.getCheckInDate(), dto.getCheckOutDate());
        long start = System.currentTimeMillis();

        ReservationDTO result = delegate.createReservation(dto);

        long elapsed = System.currentTimeMillis() - start;
        log.info("[DECORATOR] <<< createReservation() completed in {}ms. Reservation#: {}",
                elapsed, result.getReservationNumber());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public ReservationDTO findByReservationNumber(String reservationNumber) {
        log.info("[DECORATOR] >>> findByReservationNumber({})", reservationNumber);
        long start = System.currentTimeMillis();

        ReservationDTO result = delegate.findByReservationNumber(reservationNumber);

        long elapsed = System.currentTimeMillis() - start;
        log.info("[DECORATOR] <<< findByReservationNumber() completed in {}ms", elapsed);
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public List<ReservationDTO> findAllReservations() {
        log.info("[DECORATOR] >>> findAllReservations()");
        long start = System.currentTimeMillis();

        List<ReservationDTO> result = delegate.findAllReservations();

        long elapsed = System.currentTimeMillis() - start;
        log.info("[DECORATOR] <<< findAllReservations() completed in {}ms. Count: {}", elapsed, result.size());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public List<ReservationDTO> findByCustomerId(String customerId) {
        log.info("[DECORATOR] >>> findByCustomerId({})", customerId);
        long start = System.currentTimeMillis();

        List<ReservationDTO> result = delegate.findByCustomerId(customerId);

        long elapsed = System.currentTimeMillis() - start;
        log.info("[DECORATOR] <<< findByCustomerId() completed in {}ms. Count: {}", elapsed, result.size());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public List<ReservationDTO> findByDateRange(LocalDate start, LocalDate end) {
        log.info("[DECORATOR] >>> findByDateRange({}, {})", start, end);
        long startTime = System.currentTimeMillis();

        List<ReservationDTO> result = delegate.findByDateRange(start, end);

        long elapsed = System.currentTimeMillis() - startTime;
        log.info("[DECORATOR] <<< findByDateRange() completed in {}ms. Count: {}", elapsed, result.size());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public ReservationDTO updateReservationStatus(String id, ReservationStatus status) {
        log.info("[DECORATOR] >>> updateReservationStatus({}, {})", id, status);
        long start = System.currentTimeMillis();

        ReservationDTO result = delegate.updateReservationStatus(id, status);

        long elapsed = System.currentTimeMillis() - start;
        log.info("[DECORATOR] <<< updateReservationStatus() completed in {}ms", elapsed);
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public void cancelReservation(String id) {
        log.info("[DECORATOR] >>> cancelReservation({})", id);
        long start = System.currentTimeMillis();

        delegate.cancelReservation(id);

        long elapsed = System.currentTimeMillis() - start;
        log.info("[DECORATOR] <<< cancelReservation() completed in {}ms", elapsed);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRoomAvailable(String roomId, LocalDate checkIn, LocalDate checkOut) {
        log.info("[DECORATOR] >>> isRoomAvailable({}, {}, {})", roomId, checkIn, checkOut);
        long start = System.currentTimeMillis();

        boolean result = delegate.isRoomAvailable(roomId, checkIn, checkOut);

        long elapsed = System.currentTimeMillis() - start;
        log.info("[DECORATOR] <<< isRoomAvailable() completed in {}ms. Available: {}", elapsed, result);
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, RoomType type) {
        log.info("[DECORATOR] >>> findAvailableRooms({}, {}, {})", checkIn, checkOut, type);
        long start = System.currentTimeMillis();

        List<Room> result = delegate.findAvailableRooms(checkIn, checkOut, type);

        long elapsed = System.currentTimeMillis() - start;
        log.info("[DECORATOR] <<< findAvailableRooms() completed in {}ms. Count: {}", elapsed, result.size());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public ReservationDTO checkIn(String reservationNumber) {
        log.info("[DECORATOR] >>> checkIn({})", reservationNumber);
        long start = System.currentTimeMillis();

        ReservationDTO result = delegate.checkIn(reservationNumber);

        long elapsed = System.currentTimeMillis() - start;
        log.info("[DECORATOR] <<< checkIn() completed in {}ms", elapsed);
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public ReservationDTO checkOut(String reservationNumber) {
        log.info("[DECORATOR] >>> checkOut({})", reservationNumber);
        long start = System.currentTimeMillis();

        ReservationDTO result = delegate.checkOut(reservationNumber);

        long elapsed = System.currentTimeMillis() - start;
        log.info("[DECORATOR] <<< checkOut() completed in {}ms", elapsed);
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public byte[] generateReservationConfirmationPDF(String reservationId) {
        log.info("[DECORATOR] >>> generateReservationConfirmationPDF({})", reservationId);
        long start = System.currentTimeMillis();

        byte[] result = delegate.generateReservationConfirmationPDF(reservationId);

        long elapsed = System.currentTimeMillis() - start;
        log.info("[DECORATOR] <<< generateReservationConfirmationPDF() completed in {}ms. Size: {} bytes",
                elapsed, result != null ? result.length : 0);
        return result;
    }
}
