package com.oceanview.resort.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Customer entity.
 * Extends user fields with customer-specific attributes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {

    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private int loyaltyPoints;
    private boolean active;
    private LocalDateTime createdAt;
}

