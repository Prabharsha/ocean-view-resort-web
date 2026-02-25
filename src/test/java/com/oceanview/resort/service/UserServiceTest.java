package com.oceanview.resort.service;

import com.oceanview.resort.dto.UserDTO;
import com.oceanview.resort.mapper.UserMapper;
import com.oceanview.resort.model.Customer;
import com.oceanview.resort.model.User;
import com.oceanview.resort.model.enums.UserRole;
import com.oceanview.resort.repository.UserRepository;
import com.oceanview.resort.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserServiceImpl} following TDD (RED → GREEN → REFACTOR).
 *
 * <h3>Test Plan Document</h3>
 * <table>
 *   <tr><th>Test ID</th><th>Description</th><th>Preconditions</th><th>Steps</th><th>Expected</th><th>Status</th></tr>
 *   <tr><td>UST-01</td><td>Register user with valid data</td>
 *       <td>Username and email not taken</td>
 *       <td>Call registerCustomer(dto, password)</td><td>Returns UserDTO with CUSTOMER role</td><td>PASS</td></tr>
 *   <tr><td>UST-02</td><td>Duplicate username throws exception</td>
 *       <td>Username already exists</td>
 *       <td>Call registerCustomer(dto, password)</td><td>Throws IllegalStateException</td><td>PASS</td></tr>
 *   <tr><td>UST-03</td><td>Password is BCrypt encoded on registration</td>
 *       <td>Valid user data</td>
 *       <td>Call registerCustomer and capture saved entity</td><td>Saved password is encoded</td><td>PASS</td></tr>
 *   <tr><td>UST-04</td><td>Registered user gets CUSTOMER role</td>
 *       <td>Valid user data</td>
 *       <td>Call registerCustomer and capture saved entity</td><td>Role is CUSTOMER</td><td>PASS</td></tr>
 *   <tr><td>UST-05</td><td>Change password with correct old password</td>
 *       <td>User exists, old password matches</td>
 *       <td>Call changePassword(id, oldPw, newPw)</td><td>Password updated and saved</td><td>PASS</td></tr>
 *   <tr><td>UST-06</td><td>Change password with wrong old password throws exception</td>
 *       <td>User exists, old password does NOT match</td>
 *       <td>Call changePassword(id, wrongPw, newPw)</td><td>Throws IllegalArgumentException</td><td>PASS</td></tr>
 *   <tr><td>UST-07</td><td>Deactivate user sets active to false</td>
 *       <td>User exists and is active</td>
 *       <td>Call deactivateUser(id)</td><td>User isActive set to false</td><td>PASS</td></tr>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDTO validUserDTO;
    private Customer savedCustomer;
    private UserDTO resultDTO;

    @BeforeEach
    void setUp() {
        validUserDTO = UserDTO.builder()
                .username("john.smith")
                .firstName("John")
                .lastName("Smith")
                .email("john@example.com")
                .phone("+1234567890")
                .build();

        savedCustomer = new Customer();
        savedCustomer.setId("user-001");
        savedCustomer.setUsername("john.smith");
        savedCustomer.setPassword("$2a$12$encodedPasswordHash");
        savedCustomer.setFirstName("John");
        savedCustomer.setLastName("Smith");
        savedCustomer.setEmail("john@example.com");
        savedCustomer.setPhone("+1234567890");
        savedCustomer.setRole(UserRole.CUSTOMER);
        savedCustomer.setActive(true);
        savedCustomer.setLoyaltyPoints(0);

        resultDTO = UserDTO.builder()
                .id("user-001")
                .username("john.smith")
                .firstName("John")
                .lastName("Smith")
                .email("john@example.com")
                .phone("+1234567890")
                .role(UserRole.CUSTOMER)
                .isActive(true)
                .build();
    }

    // ═══════════════════════════════════════════════════════════
    // UST-01: Register user with valid data
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: UST-01
     * Description: Registering a user with valid data returns a UserDTO.
     * Preconditions: Username and email are not already taken.
     * Steps: 1) Mock checks. 2) Mock encoder and save. 3) Call registerCustomer.
     * Expected Result: Returns UserDTO with correct username and CUSTOMER role.
     * Actual Result: DTO returned correctly — PASS.
     */
    @Test
    @DisplayName("UST-01: registerCustomer with valid data returns UserDTO")
    void registerCustomer_ValidData_ReturnsUserDTO() {
        // Arrange
        when(userRepository.existsByUsername("john.smith")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("SecurePass123")).thenReturn("$2a$12$encodedPasswordHash");
        when(userRepository.save(any(User.class))).thenReturn(savedCustomer);
        when(userMapper.toDTO(any(User.class))).thenReturn(resultDTO);

        // Act
        UserDTO result = userService.registerCustomer(validUserDTO, "SecurePass123");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("john.smith");
        assertThat(result.getRole()).isEqualTo(UserRole.CUSTOMER);
        assertThat(result.isActive()).isTrue();
        verify(userRepository).save(any(User.class));
    }

    // ═══════════════════════════════════════════════════════════
    // UST-02: Duplicate username throws exception
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: UST-02
     * Description: Registering with an already-taken username throws exception.
     * Preconditions: Username "john.smith" already exists in the system.
     * Steps: 1) Mock existsByUsername to return true. 2) Call registerCustomer.
     * Expected Result: Throws IllegalStateException with "Username already taken".
     * Actual Result: Exception thrown — PASS.
     */
    @Test
    @DisplayName("UST-02: registerCustomer with duplicate username throws exception")
    void registerCustomer_DuplicateUsername_ThrowsException() {
        // Arrange
        when(userRepository.existsByUsername("john.smith")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.registerCustomer(validUserDTO, "SecurePass123"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Username already taken");

        verify(userRepository, never()).save(any());
    }

    // ═══════════════════════════════════════════════════════════
    // UST-03: Password is BCrypt encoded on registration
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: UST-03
     * Description: Verifies that the raw password is encoded before saving.
     * Preconditions: Valid user data provided.
     * Steps: 1) Call registerCustomer. 2) Capture saved entity. 3) Verify password.
     * Expected Result: Saved entity password equals the encoded value.
     * Actual Result: Password correctly encoded — PASS.
     */
    @Test
    @DisplayName("UST-03: registerCustomer encodes password with BCrypt")
    void registerCustomer_EncodesPassword() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("RawPassword123")).thenReturn("$2a$12$hashedValue");
        when(userRepository.save(any(User.class))).thenReturn(savedCustomer);
        when(userMapper.toDTO(any(User.class))).thenReturn(resultDTO);

        // Act
        userService.registerCustomer(validUserDTO, "RawPassword123");

        // Assert – capture the saved entity and verify password was encoded
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User captured = captor.getValue();
        assertThat(captured.getPassword()).isEqualTo("$2a$12$hashedValue");
        verify(passwordEncoder).encode("RawPassword123");
    }

    // ═══════════════════════════════════════════════════════════
    // UST-04: Registered user gets CUSTOMER role
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: UST-04
     * Description: Verifies that registered users are assigned the CUSTOMER role.
     * Preconditions: Valid user data provided.
     * Steps: 1) Call registerCustomer. 2) Capture saved entity. 3) Verify role.
     * Expected Result: Saved entity has UserRole.CUSTOMER.
     * Actual Result: Role correctly assigned — PASS.
     */
    @Test
    @DisplayName("UST-04: registerCustomer assigns CUSTOMER role")
    void registerCustomer_AssignsCustomerRole() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(savedCustomer);
        when(userMapper.toDTO(any(User.class))).thenReturn(resultDTO);

        // Act
        userService.registerCustomer(validUserDTO, "Password123");

        // Assert
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User captured = captor.getValue();
        assertThat(captured.getRole()).isEqualTo(UserRole.CUSTOMER);
    }

    // ═══════════════════════════════════════════════════════════
    // UST-05: Change password with correct old password
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: UST-05
     * Description: changePassword succeeds when old password matches.
     * Preconditions: User exists, old password is correct.
     * Steps: 1) Mock repository and encoder. 2) Call changePassword.
     * Expected Result: New password is encoded and saved.
     * Actual Result: Password updated — PASS.
     */
    @Test
    @DisplayName("UST-05: changePassword with correct old password succeeds")
    void changePassword_CorrectOldPassword_Succeeds() {
        // Arrange
        when(userRepository.findById("user-001")).thenReturn(Optional.of(savedCustomer));
        when(passwordEncoder.matches("OldPass123", savedCustomer.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("NewPass456")).thenReturn("$2a$12$newEncodedHash");

        // Act
        userService.changePassword("user-001", "OldPass123", "NewPass456");

        // Assert
        verify(passwordEncoder).encode("NewPass456");
        verify(userRepository).save(savedCustomer);
        assertThat(savedCustomer.getPassword()).isEqualTo("$2a$12$newEncodedHash");
    }

    // ═══════════════════════════════════════════════════════════
    // UST-06: Change password with wrong old password throws exception
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: UST-06
     * Description: changePassword throws exception when old password is incorrect.
     * Preconditions: User exists, old password does NOT match.
     * Steps: 1) Mock repository and encoder mismatch. 2) Call changePassword.
     * Expected Result: Throws IllegalArgumentException.
     * Actual Result: Exception thrown — PASS.
     */
    @Test
    @DisplayName("UST-06: changePassword with wrong old password throws exception")
    void changePassword_WrongOldPassword_ThrowsException() {
        // Arrange
        when(userRepository.findById("user-001")).thenReturn(Optional.of(savedCustomer));
        when(passwordEncoder.matches("WrongPass", savedCustomer.getPassword())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() ->
                userService.changePassword("user-001", "WrongPass", "NewPass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Current password is incorrect");

        verify(userRepository, never()).save(any());
    }

    // ═══════════════════════════════════════════════════════════
    // UST-07: Deactivate user sets active to false
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: UST-07
     * Description: deactivateUser sets user's active flag to false.
     * Preconditions: User exists and is currently active.
     * Steps: 1) Mock repository. 2) Call deactivateUser.
     * Expected Result: User isActive set to false and saved.
     * Actual Result: User deactivated — PASS.
     */
    @Test
    @DisplayName("UST-07: deactivateUser sets active to false")
    void deactivateUser_SetsActiveToFalse() {
        // Arrange
        savedCustomer.setActive(true);
        when(userRepository.findById("user-001")).thenReturn(Optional.of(savedCustomer));

        // Act
        userService.deactivateUser("user-001");

        // Assert
        assertThat(savedCustomer.isActive()).isFalse();
        verify(userRepository).save(savedCustomer);
    }
}
