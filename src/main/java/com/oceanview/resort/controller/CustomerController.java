package com.oceanview.resort.controller;

import com.oceanview.resort.dto.CustomerDTO;
import com.oceanview.resort.service.interfaces.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for customer management.
 *
 * <p>Provides endpoints to list, search, create, update and deactivate customers.
 * Used by both the Customer Management page and the New Reservation wizard.</p>
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customers", description = "Customer management endpoints")
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Lists all active customers, optionally filtered by a search query.
     */
    @GetMapping
    @Operation(summary = "List Customers", description = "Get all active customers, with optional search")
    public ResponseEntity<List<CustomerDTO>> listCustomers(
            @RequestParam(required = false) String q) {
        List<CustomerDTO> customers = (q != null && !q.isBlank())
                ? customerService.search(q)
                : customerService.findAllCustomers();
        return ResponseEntity.ok(customers);
    }

    /**
     * Returns a single customer by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get Customer", description = "Get a customer by ID")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable String id) {
        return ResponseEntity.ok(customerService.findById(id));
    }

    /**
     * Creates (registers) a new customer.
     * Username is auto-generated from firstName+lastName if not provided.
     * Password defaults to a system value since customers cannot log in.
     */
    @PostMapping
    @Operation(summary = "Create Customer", description = "Register a new customer")
    public ResponseEntity<CustomerDTO> createCustomer(
            @Valid @RequestBody Map<String, String> body) {

        String firstName = body.getOrDefault("firstName", "guest");
        String lastName  = body.getOrDefault("lastName",  "user");

        // Auto-generate a unique username: firstname.lastname + 4-digit random
        String baseUsername = (firstName + "." + lastName)
                .toLowerCase()
                .replaceAll("[^a-z0-9.]", "")
                + String.format("%04d", (int)(Math.random() * 9000) + 1000);

        CustomerDTO dto = CustomerDTO.builder()
                .username(body.getOrDefault("username", baseUsername))
                .firstName(firstName)
                .lastName(lastName)
                .email(body.get("email"))
                .phone(body.get("phone"))
                .address(body.get("address"))
                .build();

        // Customers cannot log in — use a secure random internal password
        String password = body.getOrDefault("password",
                "Cust@" + Long.toHexString(Double.doubleToLongBits(Math.random())));
        CustomerDTO created = customerService.createCustomer(dto, password);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Updates a customer's profile.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update Customer", description = "Update a customer's profile")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {

        CustomerDTO dto = CustomerDTO.builder()
                .firstName(body.get("firstName"))
                .lastName(body.get("lastName"))
                .email(body.get("email"))
                .phone(body.get("phone"))
                .address(body.get("address"))
                .build();

        return ResponseEntity.ok(customerService.updateCustomer(id, dto));
    }

    /**
     * Deactivates (soft-deletes) a customer.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate Customer", description = "Deactivate a customer account")
    public ResponseEntity<Void> deactivateCustomer(@PathVariable String id) {
        customerService.deactivateCustomer(id);
        return ResponseEntity.noContent().build();
    }
}

