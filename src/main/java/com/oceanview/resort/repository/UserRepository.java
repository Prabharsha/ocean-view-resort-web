package com.oceanview.resort.repository;

import com.oceanview.resort.model.User;
import com.oceanview.resort.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for {@link User} entity.
 * Provides standard CRUD operations and custom query methods for user data access.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Finds a user by their unique username.
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their email address.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a username already exists.
     */
    boolean existsByUsername(String username);

    /**
     * Checks if an email already exists.
     */
    boolean existsByEmail(String email);

    /**
     * Finds all users ordered by creation date descending (newest first).
     */
    List<User> findAllByOrderByCreatedAtDesc();

    /**
     * Finds all users with a specific role.
     */
    List<User> findByRole(UserRole role);

    /**
     * Finds all active users.
     */
    List<User> findByIsActiveTrue();
}
