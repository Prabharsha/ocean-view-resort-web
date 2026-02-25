package com.oceanview.resort.dao.impl;

import com.oceanview.resort.dao.interfaces.UserDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link UserDAO} using JdbcTemplate for native SQL queries.
 *
 * <p><b>Design Pattern: DAO (Data Access Object) Pattern – Concrete Implementation</b></p>
 * <p>Uses native SQL via {@link JdbcTemplate} for complex user-related queries
 * involving cross-table joins and aggregations for reports and analytics.
 * This complements the JPA {@code UserRepository} which handles standard
 * CRUD operations.</p>
 *
 * @see UserDAO
 * @see com.oceanview.resort.repository.UserRepository
 */
@Repository
@Slf4j
public class UserDAOImpl implements UserDAO {

    private final JdbcTemplate jdbcTemplate;

    public UserDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Queries the users table with its JOINED inheritance structure to
     * count users by their discriminator type (role).</p>
     */
    @Override
    public List<Map<String, Object>> getUserCountByRole() {
        log.debug("Getting user count by role");

        String sql = """
                SELECT u.role, COUNT(*) AS user_count
                FROM users u
                GROUP BY u.role
                ORDER BY u.role
                """;

        return jdbcTemplate.queryForList(sql);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Joins users with reservations to find the most frequent guests,
     * ordered by their total non-cancelled reservation count.</p>
     */
    @Override
    public List<Map<String, Object>> getTopCustomersByReservations(int limit) {
        log.debug("Getting top {} customers by reservations", limit);

        String sql = """
                SELECT u.id AS customer_id,
                       CONCAT(u.first_name, ' ', u.last_name) AS full_name,
                       u.email,
                       COUNT(r.id) AS reservation_count
                FROM users u
                JOIN reservations r ON r.customer_id = u.id
                WHERE r.status <> 'CANCELLED'
                AND u.role = 'CUSTOMER'
                GROUP BY u.id, u.first_name, u.last_name, u.email
                ORDER BY reservation_count DESC
                LIMIT ?
                """;

        return jdbcTemplate.queryForList(sql, limit);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Joins users with reservations on the staff_id foreign key to
     * calculate how many reservations each staff member has processed.</p>
     */
    @Override
    public List<Map<String, Object>> getStaffActivityStats() {
        log.debug("Getting staff activity statistics");

        String sql = """
                SELECT u.id AS staff_id,
                       CONCAT(u.first_name, ' ', u.last_name) AS full_name,
                       COUNT(r.id) AS reservations_processed
                FROM users u
                LEFT JOIN reservations r ON r.staff_id = u.id
                WHERE u.role IN ('STAFF', 'MANAGER')
                GROUP BY u.id, u.first_name, u.last_name
                ORDER BY reservations_processed DESC
                """;

        return jdbcTemplate.queryForList(sql);
    }
}
