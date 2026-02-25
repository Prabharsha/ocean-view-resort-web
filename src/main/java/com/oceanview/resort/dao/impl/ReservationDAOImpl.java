package com.oceanview.resort.dao.impl;

import com.oceanview.resort.dao.interfaces.ReservationDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link ReservationDAO} using JdbcTemplate for native SQL queries.
 *
 * <p><b>Design Pattern: DAO (Data Access Object) Pattern – Concrete Implementation</b></p>
 * <p>This class uses Spring's {@link JdbcTemplate} to execute complex native SQL
 * queries for reservation-related reports and analytics. The DAO Pattern separates
 * the data access logic from the business logic, providing a clean abstraction
 * layer over the database.</p>
 *
 * <h3>Key characteristics:</h3>
 * <ul>
 *   <li>Uses native SQL instead of JPQL for complex aggregation queries.</li>
 *   <li>Complements {@code ReservationRepository} (JPA) for standard CRUD.</li>
 *   <li>{@code @Repository} annotation makes it a Spring singleton bean and
 *       enables automatic exception translation.</li>
 * </ul>
 *
 * @see ReservationDAO
 * @see com.oceanview.resort.repository.ReservationRepository
 */
@Repository
@Slf4j
public class ReservationDAOImpl implements ReservationDAO {

    private final JdbcTemplate jdbcTemplate;

    public ReservationDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Executes a native SQL query joining reservations, bills, and payments
     * to calculate total revenue for the specified month.</p>
     */
    @Override
    public BigDecimal getMonthlyRevenue(int year, int month) {
        log.debug("Calculating monthly revenue for {}/{}", year, month);

        String sql = """
                SELECT COALESCE(SUM(p.amount_paid), 0) AS total_revenue
                FROM payments p
                JOIN reservations r ON p.reservation_id = r.id
                WHERE YEAR(p.payment_date) = ?
                AND MONTH(p.payment_date) = ?
                """;

        BigDecimal revenue = jdbcTemplate.queryForObject(sql, BigDecimal.class, year, month);
        log.debug("Monthly revenue for {}/{}: {}", year, month, revenue);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Calculates occupancy by counting rooms with active reservations
     * (CONFIRMED or CHECKED_IN) within the date range.</p>
     */
    @Override
    public Map<String, Object> getOccupancyStats(LocalDate startDate, LocalDate endDate) {
        log.debug("Calculating occupancy stats from {} to {}", startDate, endDate);

        String sql = """
                SELECT
                    (SELECT COUNT(*) FROM rooms) AS total_rooms,
                    COUNT(DISTINCT r.room_id) AS occupied_rooms,
                    ROUND(COUNT(DISTINCT r.room_id) * 100.0 / (SELECT COUNT(*) FROM rooms), 2) AS occupancy_percentage
                FROM reservations r
                WHERE r.status IN ('CONFIRMED', 'CHECKED_IN')
                AND r.check_in_date <= ?
                AND r.check_out_date >= ?
                """;

        Map<String, Object> stats = jdbcTemplate.queryForMap(sql, endDate, startDate);
        log.debug("Occupancy stats: {}", stats);
        return stats;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Groups reservation creation dates to produce a daily count breakdown.</p>
     */
    @Override
    public List<Map<String, Object>> getDailyReservationCounts(LocalDate startDate, LocalDate endDate) {
        log.debug("Getting daily reservation counts from {} to {}", startDate, endDate);

        String sql = """
                SELECT DATE(r.created_at) AS reservation_date,
                       COUNT(*) AS reservation_count
                FROM reservations r
                WHERE DATE(r.created_at) BETWEEN ? AND ?
                GROUP BY DATE(r.created_at)
                ORDER BY reservation_date
                """;

        return jdbcTemplate.queryForList(sql, startDate, endDate);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Joins reservations with rooms to count bookings per room, ordered
     * by popularity descending.</p>
     */
    @Override
    public List<Map<String, Object>> getTopBookedRooms(int limit) {
        log.debug("Getting top {} most booked rooms", limit);

        String sql = """
                SELECT rm.room_number, rm.room_type, COUNT(r.id) AS booking_count
                FROM reservations r
                JOIN rooms rm ON r.room_id = rm.id
                WHERE r.status <> 'CANCELLED'
                GROUP BY rm.id, rm.room_number, rm.room_type
                ORDER BY booking_count DESC
                LIMIT ?
                """;

        return jdbcTemplate.queryForList(sql, limit);
    }
}
