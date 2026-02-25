package com.oceanview.resort.service.impl;

import com.oceanview.resort.dto.UserDTO;
import com.oceanview.resort.mapper.UserMapper;
import com.oceanview.resort.model.Customer;
import com.oceanview.resort.model.User;
import com.oceanview.resort.model.enums.UserRole;
import com.oceanview.resort.repository.UserRepository;
import com.oceanview.resort.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link UserService}.
 *
 * <p>Handles user registration (with BCrypt password hashing), profile updates,
 * password changes, and soft-delete (deactivation).</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /** {@inheritDoc} */
    @Override
    @Transactional
    public UserDTO registerCustomer(UserDTO userDTO, String password) {
        log.info("Registering new customer: {}", userDTO.getUsername());

        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalStateException("Username already taken: " + userDTO.getUsername());
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalStateException("Email already registered: " + userDTO.getEmail());
        }

        Customer customer = new Customer();
        customer.setUsername(userDTO.getUsername());
        customer.setPassword(passwordEncoder.encode(password));
        customer.setFirstName(userDTO.getFirstName());
        customer.setLastName(userDTO.getLastName());
        customer.setEmail(userDTO.getEmail());
        customer.setPhone(userDTO.getPhone());
        customer.setRole(UserRole.CUSTOMER);
        customer.setActive(true);
        customer.setLoyaltyPoints(0);

        User saved = userRepository.save(customer);
        log.info("Customer registered: {} ({})", saved.getUsername(), saved.getId());
        return userMapper.toDTO(saved);
    }

    /** {@inheritDoc} */
    @Override
    public UserDTO findById(String id) {
        log.debug("Finding user by id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        return userMapper.toDTO(user);
    }

    /** {@inheritDoc} */
    @Override
    public UserDTO findByUsername(String username) {
        log.debug("Finding user by username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + username));
        return userMapper.toDTO(user);
    }

    /** {@inheritDoc} */
    @Override
    public List<UserDTO> findAllUsers() {
        log.debug("Finding all users");
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    public List<UserDTO> findByRole(UserRole role) {
        log.debug("Finding users by role: {}", role);
        return userRepository.findByRole(role).stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public UserDTO updateUser(String id, UserDTO userDTO) {
        log.info("Updating user: {}", id);

        User existing = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        existing.setFirstName(userDTO.getFirstName());
        existing.setLastName(userDTO.getLastName());
        existing.setEmail(userDTO.getEmail());
        existing.setPhone(userDTO.getPhone());

        User saved = userRepository.save(existing);
        log.info("User updated: {}", saved.getUsername());
        return userMapper.toDTO(saved);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void changePassword(String id, String oldPassword, String newPassword) {
        log.info("Changing password for user: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed for user: {}", user.getUsername());
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void deactivateUser(String id) {
        log.info("Deactivating user: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        user.setActive(false);
        userRepository.save(user);
        log.info("User deactivated: {}", user.getUsername());
    }

    /** {@inheritDoc} */
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /** {@inheritDoc} */
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
