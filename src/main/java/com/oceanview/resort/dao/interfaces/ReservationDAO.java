package com.oceanview.resort.dao.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object interface for Reservation entity.
 *
 * <p><b>Design Pattern: DAO (Data Access Object) Pattern</b></p>
 * <p>The DAO Pattern separates the data persistence logic from the business logic
 * by providing an abstract interface for database operations. This interface
 * defines custom data access methods that use <b>native SQL</b> via
 * {@code JdbcTemplate} — complementing the JPA {@code ReservationRepository}
 * which handles standard CRUD and JPQL queries.</p>
 *
 * <h3>When to use DAO vs Repository:</h3>
 * <ul>
 *   <li><b>Repository (JPA):</b> Standard CRUD, simple queries, JPQL, pagination.</li>
 *   <li><b>DAO (JdbcTemplate):</b> Complex native SQL, aggregate reports,
 *       cross-table joins, performance-critical queries, database-specific features.</li>
 * </ul>
 *
 * <h3>Benefits:</h3>
 * <ul>
 *   <li>Encapsulates complex SQL in a dedicated layer.</li>
 *   <li>Business logic in the service layer remains clean and testable.</li>
 *   <li>Can be swapped to a different database implementation without changing services.</li>
 * </ul>
 *
 * @see com.oceanview.resort.dao.impl.ReservationDAOImpl
 * @see com.oceanview.resort.repository.ReservationRepository
 */
public interface ReservationDAO {

    /**
     * Calculates the total revenue for a given month.
     *
     * @param year  the year (e.g. 2026)
     * @param month the month (1–12)
     * @return total revenue as BigDecimal, or BigDecimal.ZERO if none
     */
    BigDecimal getMonthlyRevenue(int year, int month);

    /**
     * Calculates room occupancy statistics for a given date range.
     * Returns: total_rooms, occupied_rooms, occupancy_percentage
     *
     * @param startDate start of the range (inclusive)
     * @param endDate   end of the range (inclusive)
     * @return a map containing occupancy metrics
     */
    Map<String, Object> getOccupancyStats(LocalDate startDate, LocalDate endDate);

    /**
     * Retrieves a daily breakdown of reservation counts for a date range.
     *
     * @param startDate start of the range
     * @param endDate   end of the range
     * @return list of maps with date and count fields
     */
    List<Map<String, Object>> getDailyReservationCounts(LocalDate startDate, LocalDate endDate);

    /**
     * Retrieves the top N most booked rooms.
     *
     * @param limit the maximum number of rooms to return
     * @return list of maps with room_number, room_type, and booking_count
     */
    List<Map<String, Object>> getTopBookedRooms(int limit);
}
