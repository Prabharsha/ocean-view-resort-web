package com.oceanview.resort.dao;

import com.oceanview.resort.config.DatabaseManager;
import com.oceanview.resort.model.Bill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO for Bill entity.
 */
public class BillDAO {

    private static final Logger log = LoggerFactory.getLogger(BillDAO.class);

    private Bill mapRow(ResultSet rs) throws SQLException {
        Bill b = new Bill();
        b.setId(rs.getString("id"));
        b.setReservationId(rs.getString("reservation_id"));
        b.setNumNights(rs.getInt("num_nights"));
        b.setRoomRate(rs.getBigDecimal("room_rate"));
        b.setSubtotal(rs.getBigDecimal("subtotal"));
        b.setTaxRate(rs.getBigDecimal("tax_rate"));
        b.setTaxAmount(rs.getBigDecimal("tax_amount"));
        b.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        b.setTotalAmount(rs.getBigDecimal("total_amount"));
        b.setPaymentStatus(rs.getString("payment_status"));
        Timestamp ts = rs.getTimestamp("generated_at");
        if (ts != null) b.setGeneratedAt(ts.toLocalDateTime());
        return b;
    }

    private Bill mapEnrichedRow(ResultSet rs) throws SQLException {
        Bill b = mapRow(rs);
        try { b.setReservationNumber(rs.getString("reservation_number")); } catch (SQLException ignored) {}
        try { b.setGuestName(rs.getString("guest_name")); } catch (SQLException ignored) {}
        try { b.setRoomNumber(rs.getString("room_number")); } catch (SQLException ignored) {}
        try { b.setRoomType(rs.getString("room_type")); } catch (SQLException ignored) {}
        try { b.setGuestEmail(rs.getString("guest_email")); } catch (SQLException ignored) {}
        return b;
    }

    private static final String ENRICHED_SELECT =
        "SELECT bi.*, res.reservation_number, res.guest_name, rm.room_number, rm.room_type, " +
        "u.email AS guest_email " +
        "FROM bills bi " +
        "JOIN reservations res ON res.id = bi.reservation_id " +
        "LEFT JOIN rooms rm ON rm.id = res.room_id " +
        "LEFT JOIN users u ON u.id = res.customer_id ";

    public Optional<Bill> findById(String id) {
        String sql = ENRICHED_SELECT + "WHERE bi.id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapEnrichedRow(rs));
        } catch (SQLException e) {
            log.error("Error finding bill by id: {}", id, e);
        }
        return Optional.empty();
    }

    public Optional<Bill> findByReservationId(String reservationId) {
        String sql = ENRICHED_SELECT + "WHERE bi.reservation_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reservationId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapEnrichedRow(rs));
        } catch (SQLException e) {
            log.error("Error finding bill by reservation: {}", reservationId, e);
        }
        return Optional.empty();
    }

    public boolean existsByReservationId(String reservationId) {
        String sql = "SELECT COUNT(*) FROM bills WHERE reservation_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reservationId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) { return false; }
    }

    public List<Bill> findAll() {
        List<Bill> list = new ArrayList<>();
        String sql = ENRICHED_SELECT + "ORDER BY bi.generated_at DESC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapEnrichedRow(rs));
        } catch (SQLException e) {
            log.error("Error finding all bills", e);
        }
        return list;
    }

    public List<Bill> findByPaymentStatus(String status) {
        List<Bill> list = new ArrayList<>();
        String sql = ENRICHED_SELECT + "WHERE bi.payment_status = ? ORDER BY bi.generated_at DESC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapEnrichedRow(rs));
        } catch (SQLException e) {
            log.error("Error finding bills by status: {}", status, e);
        }
        return list;
    }

    public String save(Bill bill) {
        String id = java.util.UUID.randomUUID().toString();
        String sql = "INSERT INTO bills (id, reservation_id, num_nights, room_rate, subtotal, " +
                     "tax_rate, tax_amount, discount_amount, total_amount, payment_status) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, bill.getReservationId());
            ps.setInt(3, bill.getNumNights());
            ps.setBigDecimal(4, bill.getRoomRate());
            ps.setBigDecimal(5, bill.getSubtotal());
            ps.setBigDecimal(6, bill.getTaxRate());
            ps.setBigDecimal(7, bill.getTaxAmount());
            ps.setBigDecimal(8, bill.getDiscountAmount());
            ps.setBigDecimal(9, bill.getTotalAmount());
            ps.setString(10, bill.getPaymentStatus());
            ps.executeUpdate();
            return id;
        } catch (SQLException e) {
            log.error("Error saving bill", e);
            throw new RuntimeException("Failed to save bill", e);
        }
    }

    public void update(Bill bill) {
        String sql = "UPDATE bills SET discount_amount=?, total_amount=?, payment_status=? WHERE id=?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, bill.getDiscountAmount());
            ps.setBigDecimal(2, bill.getTotalAmount());
            ps.setString(3, bill.getPaymentStatus());
            ps.setString(4, bill.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error updating bill: {}", bill.getId(), e);
            throw new RuntimeException("Failed to update bill", e);
        }
    }
}

