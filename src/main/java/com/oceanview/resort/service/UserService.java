package com.oceanview.resort.service;

import com.oceanview.resort.dao.CustomerDAO;
import com.oceanview.resort.dao.UserDAO;
import com.oceanview.resort.model.Customer;
import com.oceanview.resort.model.User;
import com.oceanview.resort.model.enums.UserRole;
import com.oceanview.resort.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Service Layer for user management operations.
 *
 * <p><b>Design Pattern: Service Layer Pattern</b></p>
 * <p>Contains all business logic for users, separated from presentation (servlets)
 * and data access (DAOs).</p>
 */
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserDAO userDAO;
    private final CustomerDAO customerDAO;

    public UserService(UserDAO userDAO, CustomerDAO customerDAO) {
        this.userDAO = userDAO;
        this.customerDAO = customerDAO;
    }

    /** Registers a new customer account. */
    public Customer registerCustomer(String username, String password, String firstName,
                                     String lastName, String email, String phone) {
        log.info("Registering new customer: {}", username);

        if (userDAO.existsByUsername(username)) {
            throw new IllegalStateException("Username already taken: " + username);
        }
        if (userDAO.existsByEmail(email)) {
            throw new IllegalStateException("Email already registered: " + email);
        }

        Customer customer = new Customer();
        customer.setUsername(username);
        customer.setPassword(PasswordUtil.hashPassword(password));
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setRole(UserRole.CUSTOMER);
        customer.setActive(true);
        customer.setLoyaltyPoints(0);

        String id = customerDAO.save(customer);
        customer.setId(id);
        log.info("Customer registered: {} ({})", username, id);
        return customer;
    }

    /** Authenticates a user by username and password. Returns the User or null. */
    public User authenticate(String username, String password) {
        return userDAO.findByUsername(username)
                .filter(user -> user.isActive() && PasswordUtil.verifyPassword(password, user.getPassword()))
                .orElse(null);
    }

    public User findById(String id) {
        return userDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    public User findByUsername(String username) {
        return userDAO.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }

    public List<User> findAllUsers() {
        return userDAO.findAll();
    }

    public void changePassword(String userId, String oldPassword, String newPassword) {
        User user = findById(userId);
        if (!PasswordUtil.verifyPassword(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        userDAO.updatePassword(userId, PasswordUtil.hashPassword(newPassword));
        log.info("Password changed for user: {}", user.getUsername());
    }

    public void updateUser(User user) {
        userDAO.update(user);
    }
}

