package com.oceanview.resort.dao;

import com.oceanview.resort.config.DatabaseManager;
import com.oceanview.resort.model.Customer;
import com.oceanview.resort.model.enums.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO for Customer entity (joins users + customers tables).
 *
 * <p><b>Design Pattern: DAO Pattern</b></p>
 */
public class CustomerDAO {

    private static final Logger log = LoggerFactory.getLogger(CustomerDAO.class);

    /** Maps a ResultSet row (JOIN of users + customers) to a Customer object. */
    private Customer mapRow(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setId(rs.getString("id"));
        c.setUsername(rs.getString("username"));
        c.setPassword(rs.getString("password"));
        c.setFirstName(rs.getString("first_name"));
        c.setLastName(rs.getString("last_name"));
        c.setEmail(rs.getString("email"));
        c.setPhone(rs.getString("phone"));
        c.setRole(UserRole.valueOf(rs.getString("role")));
        c.setActive(rs.getBoolean("is_active"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) c.setCreatedAt(ts.toLocalDateTime());
        c.setAddress(rs.getString("address"));
        c.setLoyaltyPoints(rs.getInt("loyalty_points"));
        return c;
    }

    private static final String SELECT_JOIN =
            "SELECT u.*, c.address, c.loyalty_points FROM users u " +
            "JOIN customers c ON c.id = u.id ";

    /** Finds a customer by their user ID. */
    public Optional<Customer> findById(String id) {
        String sql = SELECT_JOIN + "WHERE u.id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            log.error("Error finding customer by id: {}", id, e);
        }
        return Optional.empty();
    }

    /** Returns all customers ordered by name. */
    public List<Customer> findAll() {
        List<Customer> list = new ArrayList<>();
        String sql = SELECT_JOIN + "WHERE u.role = 'CUSTOMER' ORDER BY u.first_name, u.last_name";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            log.error("Error finding all customers", e);
        }
        return list;
    }

    /** Searches customers by name, email, or phone. */
    public List<Customer> search(String query) {
        List<Customer> list = new ArrayList<>();
        String sql = SELECT_JOIN +
                "WHERE u.role = 'CUSTOMER' AND (u.first_name LIKE ? OR u.last_name LIKE ? " +
                "OR u.email LIKE ? OR u.phone LIKE ?) ORDER BY u.first_name";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + query + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            log.error("Error searching customers: {}", query, e);
        }
        return list;
    }

    /** Inserts a new customer (both users + customers tables). */
    public String save(Customer customer) {
        String id = java.util.UUID.randomUUID().toString();
        String sqlUser = "INSERT INTO users (id, username, password, first_name, last_name, email, phone, role, is_active) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, 'CUSTOMER', TRUE)";
        String sqlCust = "INSERT INTO customers (id, address, loyalty_points) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(sqlUser)) {
                ps1.setString(1, id);
                ps1.setString(2, customer.getUsername());
                ps1.setString(3, customer.getPassword());
                ps1.setString(4, customer.getFirstName());
                ps1.setString(5, customer.getLastName());
                ps1.setString(6, customer.getEmail());
                ps1.setString(7, customer.getPhone());
                ps1.executeUpdate();
            }

            try (PreparedStatement ps2 = conn.prepareStatement(sqlCust)) {
                ps2.setString(1, id);
                ps2.setString(2, customer.getAddress());
                ps2.setInt(3, customer.getLoyaltyPoints());
                ps2.executeUpdate();
            }

            conn.commit();
            log.info("Customer saved: {} ({})", customer.getUsername(), id);
            return id;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { /* ignore */ }
            log.error("Error saving customer: {}", customer.getUsername(), e);
            throw new RuntimeException("Failed to save customer", e);
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { /* ignore */ }
        }
    }

    /** Updates customer-specific fields (address, loyalty points). */
    public void updateCustomerFields(String id, String address, int loyaltyPoints) {
        String sql = "UPDATE customers SET address = ?, loyalty_points = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, address);
            ps.setInt(2, loyaltyPoints);
            ps.setString(3, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error updating customer fields: {}", id, e);
            throw new RuntimeException("Failed to update customer", e);
        }
    }

    /** Updates loyalty points only. */
    public void updateLoyaltyPoints(String customerId, int points) {
        String sql = "UPDATE customers SET loyalty_points = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, points);
            ps.setString(2, customerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error updating loyalty points: {}", customerId, e);
        }
    }
}

