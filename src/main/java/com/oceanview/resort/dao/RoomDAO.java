package com.oceanview.resort.dao;

import com.oceanview.resort.config.DatabaseManager;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.enums.RoomType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO for Room entity. Uses plain JDBC with PreparedStatements.
 */
public class RoomDAO {

    private static final Logger log = LoggerFactory.getLogger(RoomDAO.class);

    private Room mapRow(ResultSet rs) throws SQLException {
        Room r = new Room();
        r.setId(rs.getString("id"));
        r.setRoomNumber(rs.getString("room_number"));
        r.setRoomType(RoomType.valueOf(rs.getString("room_type")));
        r.setFloorNumber(rs.getInt("floor_number"));
        r.setCapacity(rs.getInt("capacity"));
        r.setRatePerNight(rs.getBigDecimal("rate_per_night"));
        r.setDescription(rs.getString("description"));
        r.setAvailable(rs.getBoolean("is_available"));
        r.setAmenities(rs.getString("amenities"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) r.setCreatedAt(ts.toLocalDateTime());
        return r;
    }

    public Optional<Room> findById(String id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            log.error("Error finding room by id: {}", id, e);
        }
        return Optional.empty();
    }

    public Optional<Room> findByRoomNumber(String roomNumber) {
        String sql = "SELECT * FROM rooms WHERE room_number = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            log.error("Error finding room by number: {}", roomNumber, e);
        }
        return Optional.empty();
    }

    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_number";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) rooms.add(mapRow(rs));
        } catch (SQLException e) {
            log.error("Error finding all rooms", e);
        }
        return rooms;
    }

    /** Finds rooms available for a date range (no overlapping reservations). */
    public List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, RoomType type) {
        List<Room> rooms = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT r.* FROM rooms r WHERE r.is_available = TRUE " +
            "AND r.id NOT IN (" +
            "  SELECT res.room_id FROM reservations res " +
            "  WHERE res.status NOT IN ('CANCELLED','CHECKED_OUT') " +
            "  AND res.check_in_date < ? AND res.check_out_date > ?" +
            ")");
        if (type != null) sql.append(" AND r.room_type = ?");
        sql.append(" ORDER BY r.room_type, r.room_number");

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setDate(1, Date.valueOf(checkOut));
            ps.setDate(2, Date.valueOf(checkIn));
            if (type != null) ps.setString(3, type.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) rooms.add(mapRow(rs));
        } catch (SQLException e) {
            log.error("Error finding available rooms", e);
        }
        return rooms;
    }

    /** Searches rooms with multi-criteria filters. */
    public List<Room> searchRooms(RoomType type, BigDecimal minPrice, BigDecimal maxPrice,
                                  Integer minCapacity, Integer floor, boolean availableOnly) {
        List<Room> rooms = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM rooms WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (type != null) { sql.append(" AND room_type = ?"); params.add(type.name()); }
        if (minPrice != null) { sql.append(" AND rate_per_night >= ?"); params.add(minPrice); }
        if (maxPrice != null) { sql.append(" AND rate_per_night <= ?"); params.add(maxPrice); }
        if (minCapacity != null) { sql.append(" AND capacity >= ?"); params.add(minCapacity); }
        if (floor != null) { sql.append(" AND floor_number = ?"); params.add(floor); }
        if (availableOnly) { sql.append(" AND is_available = TRUE"); }
        sql.append(" ORDER BY rate_per_night");

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof String) ps.setString(i + 1, (String) p);
                else if (p instanceof BigDecimal) ps.setBigDecimal(i + 1, (BigDecimal) p);
                else if (p instanceof Integer) ps.setInt(i + 1, (Integer) p);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) rooms.add(mapRow(rs));
        } catch (SQLException e) {
            log.error("Error searching rooms", e);
        }
        return rooms;
    }

    public String save(Room room) {
        String id = java.util.UUID.randomUUID().toString();
        String sql = "INSERT INTO rooms (id, room_number, room_type, floor_number, capacity, " +
                     "rate_per_night, description, is_available, amenities) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, room.getRoomNumber());
            ps.setString(3, room.getRoomType().name());
            ps.setInt(4, room.getFloorNumber());
            ps.setInt(5, room.getCapacity());
            ps.setBigDecimal(6, room.getRatePerNight());
            ps.setString(7, room.getDescription());
            ps.setBoolean(8, room.isAvailable());
            ps.setString(9, room.getAmenities());
            ps.executeUpdate();
            return id;
        } catch (SQLException e) {
            log.error("Error saving room", e);
            throw new RuntimeException("Failed to save room", e);
        }
    }

    public void update(Room room) {
        String sql = "UPDATE rooms SET room_number=?, room_type=?, floor_number=?, capacity=?, " +
                     "rate_per_night=?, description=?, is_available=?, amenities=? WHERE id=?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getRoomType().name());
            ps.setInt(3, room.getFloorNumber());
            ps.setInt(4, room.getCapacity());
            ps.setBigDecimal(5, room.getRatePerNight());
            ps.setString(6, room.getDescription());
            ps.setBoolean(7, room.isAvailable());
            ps.setString(8, room.getAmenities());
            ps.setString(9, room.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error updating room: {}", room.getId(), e);
            throw new RuntimeException("Failed to update room", e);
        }
    }

    public void delete(String id) {
        String sql = "DELETE FROM rooms WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error deleting room: {}", id, e);
            throw new RuntimeException("Failed to delete room", e);
        }
    }

    public boolean existsByRoomNumber(String roomNumber) {
        String sql = "SELECT COUNT(*) FROM rooms WHERE room_number = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomNumber);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) { return false; }
    }

    public long count() {
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM rooms");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) { return 0; }
    }

    public long countAvailable() {
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM rooms WHERE is_available = TRUE");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) { return 0; }
    }
}

