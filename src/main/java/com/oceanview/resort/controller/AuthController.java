package com.oceanview.resort.controller;

import com.oceanview.resort.dto.*;
import com.oceanview.resort.security.JwtTokenProvider;
import com.oceanview.resort.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for authentication operations.
 *
 * <p>Handles user login (JWT generation), logout, registration,
 * JWT token refresh, profile retrieval, and password changes.</p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication and user management endpoints")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Authenticates a user and returns access + refresh JWT tokens.
     */
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and receive JWT tokens")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDTO user = userService.findByUsername(request.getUsername());

        String accessToken = jwtTokenProvider.generateAccessToken(
                authentication, user.getId(), user.getRole().name());
        String refreshToken = jwtTokenProvider.generateRefreshToken(
                authentication, user.getId(), user.getRole().name());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("user", user);
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("tokenType", "Bearer");

        log.info("User logged in: {}", request.getUsername());
        return ResponseEntity.ok(response);
    }

    /**
     * Logs out the current user by clearing the security context.
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalidate current session")
    public ResponseEntity<Map<String, String>> logout() {
        SecurityContextHolder.clearContext();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout successful");
        return ResponseEntity.ok(response);
    }

    /**
     * Registers a new customer account.
     */
    @PostMapping("/register")
    @Operation(summary = "Register", description = "Register a new customer account")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration attempt for user: {}", request.getUsername());

        UserDTO userDTO = UserDTO.builder()
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .build();

        UserDTO registered = userService.registerCustomer(userDTO, request.getPassword());
        log.info("User registered: {}", registered.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(registered);
    }

    /**
     * Refreshes the access token using a valid refresh token.
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Refresh an expired access token")
    public ResponseEntity<Map<String, String>> refreshToken(
            @RequestHeader("Authorization") String authHeader) {

        String refreshToken = null;
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            refreshToken = authHeader.substring(7);
        }

        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired refresh token"));
        }

        String newAccessToken = jwtTokenProvider.refreshAccessToken(refreshToken);

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", newAccessToken);
        response.put("tokenType", "Bearer");
        response.put("message", "Token refreshed successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Returns the profile of the currently authenticated user.
     */
    @GetMapping("/profile")
    @Operation(summary = "Get Profile", description = "Get current user's profile")
    public ResponseEntity<UserDTO> getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDTO user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }

    /**
     * Changes the password for the currently authenticated user.
     */
    @PutMapping("/change-password")
    @Operation(summary = "Change Password", description = "Change current user's password")
    public ResponseEntity<Map<String, String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDTO user = userService.findByUsername(username);

        userService.changePassword(user.getId(),
                request.getOldPassword(), request.getNewPassword());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password changed successfully");
        return ResponseEntity.ok(response);
    }
}
