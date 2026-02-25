package com.oceanview.resort.dao.interfaces;

import java.util.List;
import java.util.Map;

/**
 * Data Access Object interface for User entity.
 *
 * <p><b>Design Pattern: DAO (Data Access Object) Pattern</b></p>
 * <p>Defines custom data access methods using native SQL via {@code JdbcTemplate}
 * for complex user-related queries and report generation. These queries involve
 * cross-table joins and aggregations that are more naturally expressed in native SQL
 * than JPQL.</p>
 *
 * @see com.oceanview.resort.dao.impl.UserDAOImpl
 * @see com.oceanview.resort.repository.UserRepository
 */
public interface UserDAO {

    /**
     * Retrieves user statistics grouped by role (CUSTOMER, STAFF, MANAGER, MAINTENANCE).
     *
     * @return list of maps with role and count
     */
    List<Map<String, Object>> getUserCountByRole();

    /**
     * Finds the top N customers by number of reservations made.
     *
     * @param limit the maximum number of customers to return
     * @return list of maps with customer_id, full_name, email, reservation_count
     */
    List<Map<String, Object>> getTopCustomersByReservations(int limit);

    /**
     * Retrieves staff activity statistics — number of reservations processed by each staff.
     *
     * @return list of maps with staff_id, full_name, reservations_processed
     */
    List<Map<String, Object>> getStaffActivityStats();
}
