package com.oceanview.resort.dao.impl;

import com.oceanview.resort.dao.interfaces.RoomDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link RoomDAO} using JdbcTemplate for native SQL queries.
 *
 * <p><b>Design Pattern: DAO (Data Access Object) Pattern – Concrete Implementation</b></p>
 * <p>Uses native SQL via {@link JdbcTemplate} for complex room availability
 * queries and room statistics that involve multi-table joins and sub-queries.
 * These queries are more naturally expressed in native SQL than JPQL, providing
 * better performance and database-specific optimisations.</p>
 *
 * @see RoomDAO
 * @see com.oceanview.resort.repository.RoomRepository
 */
@Repository
@Slf4j
public class RoomDAOImpl implements RoomDAO {

    private final JdbcTemplate jdbcTemplate;

    public RoomDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Uses a NOT EXISTS sub-query to exclude rooms with overlapping,
     * non-cancelled reservations in the requested date range.</p>
     */
    @Override
    public List<Map<String, Object>> findAvailableRoomsNative(LocalDate checkIn, LocalDate checkOut) {
        log.debug("Finding available rooms (native SQL) from {} to {}", checkIn, checkOut);

        String sql = """
                SELECT rm.id, rm.room_number, rm.room_type, rm.rate_per_night,
                       rm.max_occupancy, rm.description
                FROM rooms rm
                WHERE rm.is_available = true
                AND NOT EXISTS (
                    SELECT 1 FROM reservations r
                    WHERE r.room_id = rm.id
                    AND r.status <> 'CANCELLED'
                    AND r.check_in_date < ?
                    AND r.check_out_date > ?
                )
                ORDER BY rm.room_type, rm.room_number
                """;

        List<Map<String, Object>> rooms = jdbcTemplate.queryForList(sql, checkOut, checkIn);
        log.debug("Found {} available rooms", rooms.size());
        return rooms;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Groups rooms by type with aggregate statistics including average rate
     * and current occupancy count (rooms with CHECKED_IN reservations today).</p>
     */
    @Override
    public List<Map<String, Object>> getRoomStatsByType() {
        log.debug("Getting room statistics by type");

        String sql = """
                SELECT rm.room_type,
                       COUNT(*) AS total_rooms,
                       ROUND(AVG(rm.rate_per_night), 2) AS avg_rate,
                       (SELECT COUNT(DISTINCT r.room_id)
                        FROM reservations r
                        WHERE r.status = 'CHECKED_IN'
                        AND r.room_id IN (SELECT rm2.id FROM rooms rm2 WHERE rm2.room_type = rm.room_type)
                       ) AS occupied_count
                FROM rooms rm
                GROUP BY rm.room_type
                ORDER BY rm.room_type
                """;

        return jdbcTemplate.queryForList(sql);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Retrieves reservation history for a specific room including guest
     * name, dates, and status — useful for maintenance planning reports.</p>
     */
    @Override
    public List<Map<String, Object>> getRoomMaintenanceHistory(String roomId) {
        log.debug("Getting maintenance history for room: {}", roomId);

        String sql = """
                SELECT r.reservation_number, r.guest_name, r.check_in_date,
                       r.check_out_date, r.status, r.special_requests
                FROM reservations r
                WHERE r.room_id = ?
                ORDER BY r.check_in_date DESC
                LIMIT 20
                """;

        return jdbcTemplate.queryForList(sql, roomId);
    }
}
