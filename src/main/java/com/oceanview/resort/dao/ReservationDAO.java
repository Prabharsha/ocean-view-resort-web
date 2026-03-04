package com.oceanview.resort.dao;

import com.oceanview.resort.config.DatabaseManager;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.enums.ReservationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO for Reservation entity. Uses plain JDBC with PreparedStatements.
 */
public class ReservationDAO {

    private static final Logger log = LoggerFactory.getLogger(ReservationDAO.class);

    private Reservation mapRow(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setId(rs.getString("id"));
        r.setReservationNumber(rs.getString("reservation_number"));
        r.setCustomerId(rs.getString("customer_id"));
        r.setRoomId(rs.getString("room_id"));
        r.setStaffId(rs.getString("staff_id"));
        r.setGuestName(rs.getString("guest_name"));
        r.setGuestAddress(rs.getString("guest_address"));
        r.setGuestContact(rs.getString("guest_contact"));
        Date ci = rs.getDate("check_in_date");
        if (ci != null) r.setCheckInDate(ci.toLocalDate());
        Date co = rs.getDate("check_out_date");
        if (co != null) r.setCheckOutDate(co.toLocalDate());
        r.setNumGuests(rs.getInt("num_guests"));
        r.setSpecialRequests(rs.getString("special_requests"));
        r.setStatus(ReservationStatus.valueOf(rs.getString("status")));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) r.setCreatedAt(ts.toLocalDateTime());
        Timestamp ts2 = rs.getTimestamp("updated_at");
        if (ts2 != null) r.setUpdatedAt(ts2.toLocalDateTime());
        return r;
    }

    /** Maps a row from an enriched query that JOINs customer name, room number, etc. */
    private Reservation mapEnrichedRow(ResultSet rs) throws SQLException {
        Reservation r = mapRow(rs);
        try { r.setCustomerName(rs.getString("customer_name")); } catch (SQLException ignored) {}
        try { r.setRoomNumber(rs.getString("room_number")); } catch (SQLException ignored) {}
        try { r.setRoomType(rs.getString("room_type")); } catch (SQLException ignored) {}
        try { r.setBillId(rs.getString("bill_id")); } catch (SQLException ignored) {}
        return r;
    }

    private static final String ENRICHED_SELECT =
        "SELECT res.*, " +
        "CONCAT(u.first_name, ' ', u.last_name) AS customer_name, " +
        "rm.room_number, rm.room_type, b.id AS bill_id " +
        "FROM reservations res " +
        "LEFT JOIN users u ON u.id = res.customer_id " +
        "LEFT JOIN rooms rm ON rm.id = res.room_id " +
        "LEFT JOIN bills b ON b.reservation_id = res.id ";

    public Optional<Reservation> findById(String id) {
        String sql = ENRICHED_SELECT + "WHERE res.id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapEnrichedRow(rs));
        } catch (SQLException e) {
            log.error("Error finding reservation by id: {}", id, e);
        }
        return Optional.empty();
    }

    public Optional<Reservation> findByReservationNumber(String num) {
        String sql = ENRICHED_SELECT + "WHERE res.reservation_number = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, num);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapEnrichedRow(rs));
        } catch (SQLException e) {
            log.error("Error finding reservation by number: {}", num, e);
        }
        return Optional.empty();
    }

    public List<Reservation> findAll() {
        List<Reservation> list = new ArrayList<>();
        String sql = ENRICHED_SELECT + "ORDER BY res.created_at DESC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapEnrichedRow(rs));
        } catch (SQLException e) {
            log.error("Error finding all reservations", e);
        }
        return list;
    }

    public List<Reservation> findByCustomerId(String customerId) {
        List<Reservation> list = new ArrayList<>();
        String sql = ENRICHED_SELECT + "WHERE res.customer_id = ? ORDER BY res.created_at DESC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapEnrichedRow(rs));
        } catch (SQLException e) {
            log.error("Error finding reservations by customer: {}", customerId, e);
        }
        return list;
    }

    public List<Reservation> findByCheckInDateBetween(LocalDate start, LocalDate end) {
        List<Reservation> list = new ArrayList<>();
        String sql = ENRICHED_SELECT + "WHERE res.check_in_date BETWEEN ? AND ? ORDER BY res.check_in_date";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapEnrichedRow(rs));
        } catch (SQLException e) {
            log.error("Error finding reservations by date range", e);
        }
        return list;
    }

    /** Checks for overlapping active reservations for a room. */
    public boolean existsOverlappingReservation(String roomId, LocalDate checkIn, LocalDate checkOut) {
        String sql = "SELECT COUNT(*) FROM reservations WHERE room_id = ? " +
                     "AND status NOT IN ('CANCELLED','CHECKED_OUT') " +
                     "AND check_in_date < ? AND check_out_date > ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId);
            ps.setDate(2, Date.valueOf(checkOut));
            ps.setDate(3, Date.valueOf(checkIn));
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            log.error("Error checking overlapping reservation", e);
            return true; // Fail safe: assume occupied
        }
    }

    public String save(Reservation r) {
        String id = java.util.UUID.randomUUID().toString();
        String sql = "INSERT INTO reservations (id, reservation_number, customer_id, room_id, staff_id, " +
                     "guest_name, guest_address, guest_contact, check_in_date, check_out_date, " +
                     "num_guests, special_requests, status) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, r.getReservationNumber());
            ps.setString(3, r.getCustomerId());
            ps.setString(4, r.getRoomId());
            ps.setString(5, r.getStaffId());
            ps.setString(6, r.getGuestName());
            ps.setString(7, r.getGuestAddress());
            ps.setString(8, r.getGuestContact());
            ps.setDate(9, Date.valueOf(r.getCheckInDate()));
            ps.setDate(10, Date.valueOf(r.getCheckOutDate()));
            ps.setInt(11, r.getNumGuests());
            ps.setString(12, r.getSpecialRequests());
            ps.setString(13, r.getStatus().name());
            ps.executeUpdate();
            return id;
        } catch (SQLException e) {
            log.error("Error saving reservation", e);
            throw new RuntimeException("Failed to save reservation", e);
        }
    }

    public void updateStatus(String id, ReservationStatus status) {
        String sql = "UPDATE reservations SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error updating reservation status: {}", id, e);
            throw new RuntimeException("Failed to update reservation status", e);
        }
    }

    /** Finds the maximum sequence number used in reservation numbers for a given year. */
    public long findMaxSequenceByYear(int year) {
        String prefix = "OVR-" + year + "-";
        String sql = "SELECT MAX(CAST(SUBSTRING(reservation_number, " + (prefix.length() + 1) +
                     ") AS UNSIGNED)) FROM reservations WHERE reservation_number LIKE ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            log.error("Error finding max reservation sequence", e);
        }
        return 0;
    }
}

