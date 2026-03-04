package com.oceanview.resort.service;

import com.oceanview.resort.dao.CustomerDAO;
import com.oceanview.resort.dao.UserDAO;
import com.oceanview.resort.model.Customer;
import com.oceanview.resort.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Service Layer for customer management (staff/manager operations).
 */
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerDAO customerDAO;
    private final UserDAO userDAO;

    public CustomerService(CustomerDAO customerDAO, UserDAO userDAO) {
        this.customerDAO = customerDAO;
        this.userDAO = userDAO;
    }

    public List<Customer> findAll() { return customerDAO.findAll(); }

    public Customer findById(String id) {
        return customerDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
    }

    public List<Customer> search(String query) {
        if (query == null || query.isBlank()) return customerDAO.findAll();
        return customerDAO.search(query);
    }

    /** Creates a new customer from staff/manager side (auto-generates username/password). */
    public Customer createCustomer(String firstName, String lastName, String email,
                                   String phone, String address) {
        if (userDAO.existsByEmail(email)) {
            throw new IllegalStateException("Email already registered: " + email);
        }

        // Generate a username from email prefix
        String username = email.split("@")[0].replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        // Ensure uniqueness
        String baseUsername = username;
        int counter = 1;
        while (userDAO.existsByUsername(username)) {
            username = baseUsername + counter++;
        }

        Customer customer = new Customer();
        customer.setUsername(username);
        customer.setPassword(PasswordUtil.hashPassword("Customer@123")); // Default password
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setAddress(address);
        customer.setLoyaltyPoints(0);

        String id = customerDAO.save(customer);
        customer.setId(id);
        log.info("Customer created by staff: {} ({})", email, id);
        return customer;
    }
}

