package com.oceanview.resort.service.impl;

import com.oceanview.resort.dto.CustomerDTO;
import com.oceanview.resort.model.Customer;
import com.oceanview.resort.model.enums.UserRole;
import com.oceanview.resort.repository.CustomerRepository;
import com.oceanview.resort.repository.UserRepository;
import com.oceanview.resort.service.interfaces.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link CustomerService}.
 * Handles customer registration, search, update, and soft-delete.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<CustomerDTO> findAllCustomers() {
        return customerRepository.findAllActiveOrderByFirstName()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public CustomerDTO findById(String id) {
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
        return toDTO(c);
    }

    @Override
    public List<CustomerDTO> search(String query) {
        if (query == null || query.isBlank()) return findAllCustomers();
        return customerRepository.search(query.trim())
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CustomerDTO createCustomer(CustomerDTO dto, String rawPassword) {
        log.info("Creating customer: {}", dto.getUsername());

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalStateException("Username already taken: " + dto.getUsername());
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalStateException("Email already registered: " + dto.getEmail());
        }

        Customer customer = new Customer();
        customer.setUsername(dto.getUsername());
        customer.setPassword(passwordEncoder.encode(rawPassword));
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        customer.setRole(UserRole.CUSTOMER);
        customer.setActive(true);
        customer.setLoyaltyPoints(0);

        Customer saved = customerRepository.save(customer);
        log.info("Customer created: {} ({})", saved.getUsername(), saved.getId());
        return toDTO(saved);
    }

    @Override
    @Transactional
    public CustomerDTO updateCustomer(String id, CustomerDTO dto) {
        log.info("Updating customer: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));

        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());

        Customer saved = customerRepository.save(customer);
        log.info("Customer updated: {}", saved.getUsername());
        return toDTO(saved);
    }

    @Override
    @Transactional
    public void deactivateCustomer(String id) {
        log.info("Deactivating customer: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
        customer.setActive(false);
        customerRepository.save(customer);
    }

    // ── Mapping helper ────────────────────────────────────────────────────────
    private CustomerDTO toDTO(Customer c) {
        return CustomerDTO.builder()
                .id(c.getId())
                .username(c.getUsername())
                .firstName(c.getFirstName())
                .lastName(c.getLastName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .address(c.getAddress())
                .loyaltyPoints(c.getLoyaltyPoints())
                .active(c.isActive())
                .build();
    }
}

