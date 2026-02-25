package com.oceanview.resort.repository;

import com.oceanview.resort.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA Repository for {@link Customer} entity.
 * Provides data access for customer-specific queries on top of the
 * {@link UserRepository} base operations.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    /**
     * Finds a customer by the associated user's email.
     */
    Optional<Customer> findByEmail(String email);

    /**
     * Finds a customer by the associated user's username.
     */
    Optional<Customer> findByUsername(String username);
}
