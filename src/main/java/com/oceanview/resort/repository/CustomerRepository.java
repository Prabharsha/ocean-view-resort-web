package com.oceanview.resort.repository;

import com.oceanview.resort.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    /**
     * Searches customers by name, username, email or phone (case-insensitive).
     */
    @Query("SELECT c FROM Customer c WHERE c.isActive = true AND (" +
           "LOWER(c.firstName) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(c.lastName)  LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(c.username)  LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(c.email)     LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "c.phone            LIKE CONCAT('%',:q,'%'))")
    List<Customer> search(@Param("q") String query);

    /**
     * Finds all active customers ordered by first name.
     */
    @Query("SELECT c FROM Customer c WHERE c.isActive = true ORDER BY c.firstName ASC")
    List<Customer> findAllActiveOrderByFirstName();

    /**
     * Finds all active customers ordered by creation date descending (newest first).
     */
    @Query("SELECT c FROM Customer c WHERE c.isActive = true ORDER BY c.createdAt DESC")
    List<Customer> findAllActiveOrderByCreatedAtDesc();
}
