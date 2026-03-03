package com.oceanview.resort.dto;

import com.oceanview.resort.model.enums.UserRole;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for User entity.
 * Used for transferring user data between layers without exposing entity internals.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private UserRole role;
    private boolean isActive;
    private LocalDateTime createdAt;
}
