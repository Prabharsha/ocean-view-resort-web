package com.oceanview.resort.service.interfaces;

import com.oceanview.resort.dto.CustomerDTO;

import java.util.List;

/**
 * Service interface for customer management operations.
 */
public interface CustomerService {

    /** Returns all active customers. */
    List<CustomerDTO> findAllCustomers();

    /** Finds a customer by ID. */
    CustomerDTO findById(String id);

    /** Searches customers by name / username / email / phone. */
    List<CustomerDTO> search(String query);

    /** Registers a brand-new customer (staff/manager action). */
    CustomerDTO createCustomer(CustomerDTO dto, String rawPassword);

    /** Updates an existing customer's profile. */
    CustomerDTO updateCustomer(String id, CustomerDTO dto);

    /** Deactivates (soft-deletes) a customer. */
    void deactivateCustomer(String id);
}

