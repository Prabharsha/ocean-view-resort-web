package com.oceanview.resort.service.interfaces;

import com.oceanview.resort.dto.UserDTO;
import com.oceanview.resort.model.enums.UserRole;

import java.util.List;

/**
 * Service interface for user management operations.
 * Provides methods for user registration, authentication, and profile management.
 */
public interface UserService {

    /**
     * Registers a new customer account.
     */
    UserDTO registerCustomer(UserDTO userDTO, String password);

    /**
     * Finds a user by their UUID.
     */
    UserDTO findById(String id);

    /**
     * Finds a user by their username.
     */
    UserDTO findByUsername(String username);

    /**
     * Returns all users.
     */
    List<UserDTO> findAllUsers();

    /**
     * Finds users by role.
     */
    List<UserDTO> findByRole(UserRole role);

    /**
     * Updates a user's profile.
     */
    UserDTO updateUser(String id, UserDTO userDTO);

    /**
     * Changes a user's password.
     */
    void changePassword(String id, String oldPassword, String newPassword);

    /**
     * Deactivates (soft-deletes) a user account.
     */
    void deactivateUser(String id);

    /**
     * Checks if a username is already taken.
     */
    boolean existsByUsername(String username);

    /**
     * Checks if an email is already taken.
     */
    boolean existsByEmail(String email);
}
