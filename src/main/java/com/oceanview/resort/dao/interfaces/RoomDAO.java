package com.oceanview.resort.dao.interfaces;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object interface for Room entity.
 *
 * <p><b>Design Pattern: DAO (Data Access Object) Pattern</b></p>
 * <p>Defines custom data access methods using native SQL via {@code JdbcTemplate}
 * for complex room queries that go beyond what JPA repository methods can express
 * efficiently. This includes multi-table joins for availability checking and
 * aggregate statistics.</p>
 *
 * @see com.oceanview.resort.dao.impl.RoomDAOImpl
 * @see com.oceanview.resort.repository.RoomRepository
 */
public interface RoomDAO {

    /**
     * Finds all available rooms for a given date range using native SQL
     * with complex sub-query for overlapping reservations.
     *
     * @param checkIn  the check-in date
     * @param checkOut the check-out date
     * @return list of maps containing room details (id, room_number, room_type, rate)
     */
    List<Map<String, Object>> findAvailableRoomsNative(LocalDate checkIn, LocalDate checkOut);

    /**
     * Retrieves room statistics by room type — total rooms, average rate,
     * and current occupancy count.
     *
     * @return list of maps with room_type, total_rooms, avg_rate, occupied_count
     */
    List<Map<String, Object>> getRoomStatsByType();

    /**
     * Gets the maintenance history for a specific room.
     *
     * @param roomId the room ID
     * @return list of maps with maintenance details
     */
    List<Map<String, Object>> getRoomMaintenanceHistory(String roomId);
}
