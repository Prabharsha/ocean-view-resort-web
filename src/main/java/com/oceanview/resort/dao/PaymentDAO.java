package com.oceanview.resort.dao;

import com.oceanview.resort.config.DatabaseManager;
import com.oceanview.resort.model.Payment;
import com.oceanview.resort.model.enums.PaymentMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Payment entity.
 */
public class PaymentDAO {

    private static final Logger log = LoggerFactory.getLogger(PaymentDAO.class);

    private Payment mapRow(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setId(rs.getString("id"));
        p.setBillId(rs.getString("bill_id"));
        p.setReservationId(rs.getString("reservation_id"));
        p.setAmountPaid(rs.getBigDecimal("amount_paid"));
        p.setPaymentMethod(PaymentMethod.valueOf(rs.getString("payment_method")));
        p.setTransactionReference(rs.getString("transaction_reference"));
        Timestamp ts = rs.getTimestamp("payment_date");
        if (ts != null) p.setPaymentDate(ts.toLocalDateTime());
        p.setProcessedBy(rs.getString("processed_by"));
        p.setNotes(rs.getString("notes"));
        return p;
    }

    public List<Payment> findByBillId(String billId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE bill_id = ? ORDER BY payment_date DESC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, billId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            log.error("Error finding payments by bill: {}", billId, e);
        }
        return list;
    }

    /** Returns the total amount paid for a bill. */
    public BigDecimal getTotalPaidForBill(String billId) {
        String sql = "SELECT COALESCE(SUM(amount_paid), 0) FROM payments WHERE bill_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, billId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getBigDecimal(1);
        } catch (SQLException e) {
            log.error("Error getting total paid for bill: {}", billId, e);
            return BigDecimal.ZERO;
        }
    }

    public String save(Payment payment) {
        String id = java.util.UUID.randomUUID().toString();
        String sql = "INSERT INTO payments (id, bill_id, reservation_id, amount_paid, " +
                     "payment_method, transaction_reference, processed_by, notes) " +
                     "VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, payment.getBillId());
            ps.setString(3, payment.getReservationId());
            ps.setBigDecimal(4, payment.getAmountPaid());
            ps.setString(5, payment.getPaymentMethod().name());
            ps.setString(6, payment.getTransactionReference());
            ps.setString(7, payment.getProcessedBy());
            ps.setString(8, payment.getNotes());
            ps.executeUpdate();
            return id;
        } catch (SQLException e) {
            log.error("Error saving payment", e);
            throw new RuntimeException("Failed to save payment", e);
        }
    }
}

