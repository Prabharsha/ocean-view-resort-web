package com.oceanview.resort.dao;

import com.oceanview.resort.config.DatabaseManager;
import com.oceanview.resort.model.User;
import com.oceanview.resort.model.enums.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for User entity.
 *
 * <p><b>Design Pattern: DAO (Data Access Object) Pattern</b></p>
 * <p>Encapsulates all database access logic for the users table using
 * plain JDBC with PreparedStatements for SQL injection prevention.</p>
 */
public class UserDAO {

    private static final Logger log = LoggerFactory.getLogger(UserDAO.class);

    /** Maps a ResultSet row to a User object. */
    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getString("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setFirstName(rs.getString("first_name"));
        u.setLastName(rs.getString("last_name"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setRole(UserRole.valueOf(rs.getString("role")));
        u.setActive(rs.getBoolean("is_active"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) u.setCreatedAt(ts.toLocalDateTime());
        Timestamp ts2 = rs.getTimestamp("updated_at");
        if (ts2 != null) u.setUpdatedAt(ts2.toLocalDateTime());
        return u;
    }

    /** Finds a user by UUID. */
    public Optional<User> findById(String id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            log.error("Error finding user by id: {}", id, e);
        }
        return Optional.empty();
    }

    /** Finds a user by username. */
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            log.error("Error finding user by username: {}", username, e);
        }
        return Optional.empty();
    }

    /** Finds a user by email. */
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            log.error("Error finding user by email: {}", email, e);
        }
        return Optional.empty();
    }

    /** Returns all users ordered by creation date desc. */
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) users.add(mapRow(rs));
        } catch (SQLException e) {
            log.error("Error finding all users", e);
        }
        return users;
    }

    /** Returns users by role. */
    public List<User> findByRole(UserRole role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) users.add(mapRow(rs));
        } catch (SQLException e) {
            log.error("Error finding users by role: {}", role, e);
        }
        return users;
    }

    /** Inserts a new user. Returns the generated UUID. */
    public String save(User user) {
        String id = java.util.UUID.randomUUID().toString();
        String sql = "INSERT INTO users (id, username, password, first_name, last_name, email, phone, role, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getFirstName());
            ps.setString(5, user.getLastName());
            ps.setString(6, user.getEmail());
            ps.setString(7, user.getPhone());
            ps.setString(8, user.getRole().name());
            ps.setBoolean(9, user.isActive());
            ps.executeUpdate();
            log.info("User saved: {} ({})", user.getUsername(), id);
            return id;
        } catch (SQLException e) {
            log.error("Error saving user: {}", user.getUsername(), e);
            throw new RuntimeException("Failed to save user", e);
        }
    }

    /** Updates an existing user (excluding password). */
    public void update(User user) {
        String sql = "UPDATE users SET first_name=?, last_name=?, email=?, phone=?, is_active=? WHERE id=?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhone());
            ps.setBoolean(5, user.isActive());
            ps.setString(6, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error updating user: {}", user.getId(), e);
            throw new RuntimeException("Failed to update user", e);
        }
    }

    /** Updates just the password for a user. */
    public void updatePassword(String userId, String hashedPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hashedPassword);
            ps.setString(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error updating password for user: {}", userId, e);
            throw new RuntimeException("Failed to update password", e);
        }
    }

    /** Checks if a username exists. */
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            log.error("Error checking username: {}", username, e);
            return false;
        }
    }

    /** Checks if an email exists. */
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            log.error("Error checking email: {}", email, e);
            return false;
        }
    }
}

