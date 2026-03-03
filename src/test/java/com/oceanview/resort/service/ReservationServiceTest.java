package com.oceanview.resort.service;

import com.oceanview.resort.dto.ReservationDTO;
import com.oceanview.resort.exception.ReservationNotFoundException;
import com.oceanview.resort.exception.RoomNotAvailableException;
import com.oceanview.resort.mapper.ReservationMapper;
import com.oceanview.resort.model.Customer;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.enums.ReservationStatus;
import com.oceanview.resort.model.enums.RoomType;
import com.oceanview.resort.model.enums.UserRole;
import com.oceanview.resort.repository.ReservationRepository;
import com.oceanview.resort.repository.RoomRepository;
import com.oceanview.resort.repository.UserRepository;
import com.oceanview.resort.service.impl.ReservationServiceImpl;
import com.oceanview.resort.util.ReservationNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ReservationServiceImpl} following TDD (RED → GREEN → REFACTOR).
 *
 * <h3>Test Plan Document</h3>
 * <table>
 *   <tr><th>Test ID</th><th>Description</th><th>Preconditions</th><th>Steps</th><th>Expected</th><th>Status</th></tr>
 *   <tr><td>RST-01</td><td>Create reservation with valid data</td>
 *       <td>Valid DTO, room available, customer exists</td>
 *       <td>Call createReservation(dto)</td><td>Returns ReservationDTO with PENDING status and generated number</td><td>PASS</td></tr>
 *   <tr><td>RST-02</td><td>Room not available throws exception</td>
 *       <td>Valid DTO, room has overlapping reservation</td>
 *       <td>Call createReservation(dto)</td><td>Throws RoomNotAvailableException</td><td>PASS</td></tr>
 *   <tr><td>RST-03</td><td>Invalid dates (checkout before checkin)</td>
 *       <td>DTO with checkOut before checkIn</td>
 *       <td>Call createReservation(dto)</td><td>Throws IllegalArgumentException</td><td>PASS</td></tr>
 *   <tr><td>RST-04</td><td>Find by reservation number – exists</td>
 *       <td>Reservation exists in DB</td>
 *       <td>Call findByReservationNumber(number)</td><td>Returns correct DTO</td><td>PASS</td></tr>
 *   <tr><td>RST-05</td><td>Find by reservation number – not found</td>
 *       <td>No reservation with given number</td>
 *       <td>Call findByReservationNumber(invalid)</td><td>Throws ReservationNotFoundException</td><td>PASS</td></tr>
 *   <tr><td>RST-06</td><td>Cancel reservation updates status</td>
 *       <td>Reservation in PENDING status</td>
 *       <td>Call cancelReservation(id)</td><td>Status changed to CANCELLED</td><td>PASS</td></tr>
 *   <tr><td>RST-07</td><td>Check-in changes status</td>
 *       <td>Reservation in CONFIRMED status</td>
 *       <td>Call checkIn(reservationNumber)</td><td>Status changed to CHECKED_IN, event published</td><td>PASS</td></tr>
 *   <tr><td>RST-08</td><td>Generate unique reservation numbers</td>
 *       <td>ReservationNumberGenerator initialized</td>
 *       <td>Call generateNext() multiple times</td><td>Each number is unique</td><td>PASS</td></tr>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private ReservationNumberGenerator reservationNumberGenerator;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private ReservationDTO validDTO;
    private Customer testCustomer;
    private Room testRoom;
    private Reservation testReservation;
    private ReservationDTO resultDTO;

    @BeforeEach
    void setUp() {
        // Prepare test customer
        testCustomer = new Customer();
        testCustomer.setId("cust-001");
        testCustomer.setUsername("john.smith");
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Smith");
        testCustomer.setEmail("john@example.com");
        testCustomer.setRole(UserRole.CUSTOMER);

        // Prepare test room
        testRoom = Room.builder()
                .id("room-101")
                .roomNumber("101")
                .roomType(RoomType.DELUXE)
                .floorNumber(1)
                .capacity(2)
                .ratePerNight(new BigDecimal("150.00"))
                .isAvailable(true)
                .build();

        // Prepare valid DTO
        validDTO = ReservationDTO.builder()
                .customerId("cust-001")
                .roomId("room-101")
                .guestName("John Smith")
                .guestContact("+1234567890")
                .checkInDate(LocalDate.now().plusDays(1))
                .checkOutDate(LocalDate.now().plusDays(4))
                .numGuests(2)
                .build();

        // Prepare expected reservation entity
        testReservation = Reservation.builder()
                .id("res-001")
                .reservationNumber("OVR-2026-000001")
                .customer(testCustomer)
                .room(testRoom)
                .guestName("John Smith")
                .guestContact("+1234567890")
                .checkInDate(LocalDate.now().plusDays(1))
                .checkOutDate(LocalDate.now().plusDays(4))
                .numGuests(2)
                .status(ReservationStatus.PENDING)
                .build();

        // Prepare result DTO
        resultDTO = ReservationDTO.builder()
                .id("res-001")
                .reservationNumber("OVR-2026-000001")
                .customerId("cust-001")
                .roomId("room-101")
                .guestName("John Smith")
                .guestContact("+1234567890")
                .checkInDate(LocalDate.now().plusDays(1))
                .checkOutDate(LocalDate.now().plusDays(4))
                .numGuests(2)
                .status(ReservationStatus.PENDING)
                .build();
    }

    // ═══════════════════════════════════════════════════════════
    // RST-01: Create reservation successfully
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: RST-01
     * Description: Create reservation with valid data returns ReservationDTO.
     * Preconditions: Valid DTO provided, room available, customer exists.
     * Steps: 1) Mock room availability (no overlap). 2) Mock customer and room lookup.
     *         3) Mock mapper and repository save. 4) Call createReservation.
     * Expected Result: Returns DTO with PENDING status and generated reservation number.
     * Actual Result: Returns expected DTO — PASS.
     */
    @Test
    @DisplayName("RST-01: createReservation with valid data returns ReservationDTO")
    void createReservation_ValidData_ReturnsReservationDTO() {
        // Arrange
        when(reservationRepository.existsOverlappingReservation(anyString(), any(), any()))
                .thenReturn(false);
        when(userRepository.findById("cust-001")).thenReturn(Optional.of(testCustomer));
        when(roomRepository.findById("room-101")).thenReturn(Optional.of(testRoom));
        when(reservationMapper.toEntity(any(ReservationDTO.class))).thenReturn(testReservation);
        when(reservationNumberGenerator.generateNext()).thenReturn("OVR-2026-000001");
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);
        when(reservationMapper.toDTO(any(Reservation.class))).thenReturn(resultDTO);

        // Act
        ReservationDTO result = reservationService.createReservation(validDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getReservationNumber()).isEqualTo("OVR-2026-000001");
        assertThat(result.getStatus()).isEqualTo(ReservationStatus.PENDING);
        assertThat(result.getCustomerId()).isEqualTo("cust-001");
        assertThat(result.getRoomId()).isEqualTo("room-101");

        verify(reservationRepository).save(any(Reservation.class));
        verify(eventPublisher).publishEvent(any());
    }

    // ═══════════════════════════════════════════════════════════
    // RST-02: Room not available throws exception
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: RST-02
     * Description: Creating reservation when room is unavailable throws exception.
     * Preconditions: Room has an overlapping reservation for the requested dates.
     * Steps: 1) Mock overlap check to return true. 2) Call createReservation.
     * Expected Result: Throws RoomNotAvailableException.
     * Actual Result: Exception thrown — PASS.
     */
    @Test
    @DisplayName("RST-02: createReservation with unavailable room throws RoomNotAvailableException")
    void createReservation_RoomNotAvailable_ThrowsException() {
        // Arrange
        when(reservationRepository.existsOverlappingReservation(anyString(), any(), any()))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> reservationService.createReservation(validDTO))
                .isInstanceOf(RoomNotAvailableException.class);

        verify(reservationRepository, never()).save(any());
    }

    // ═══════════════════════════════════════════════════════════
    // RST-03: Invalid dates (checkout before checkin)
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: RST-03
     * Description: Creating reservation with check-out before check-in throws exception.
     * Preconditions: DTO has checkOutDate before checkInDate.
     * Steps: 1) Set invalid dates on DTO. 2) Call createReservation.
     * Expected Result: Throws IllegalArgumentException.
     * Actual Result: Exception thrown — PASS.
     */
    @Test
    @DisplayName("RST-03: createReservation with invalid dates throws IllegalArgumentException")
    void createReservation_InvalidDates_ThrowsValidationException() {
        // Arrange – checkout before checkin
        validDTO.setCheckInDate(LocalDate.now().plusDays(5));
        validDTO.setCheckOutDate(LocalDate.now().plusDays(2));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.createReservation(validDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Check-out date must be after check-in date");

        verify(reservationRepository, never()).save(any());
    }

    // ═══════════════════════════════════════════════════════════
    // RST-04: Find by reservation number – exists
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: RST-04
     * Description: Finding an existing reservation by number returns the correct DTO.
     * Preconditions: A reservation with number OVR-2026-000001 exists in the repository.
     * Steps: 1) Mock repository to return reservation. 2) Call findByReservationNumber.
     * Expected Result: Returns DTO with matching reservation number.
     * Actual Result: Correct DTO returned — PASS.
     */
    @Test
    @DisplayName("RST-04: findByReservationNumber with existing number returns DTO")
    void findByReservationNumber_Exists_ReturnsDTO() {
        // Arrange
        when(reservationRepository.findByReservationNumber("OVR-2026-000001"))
                .thenReturn(Optional.of(testReservation));
        when(reservationMapper.toDTO(testReservation)).thenReturn(resultDTO);

        // Act
        ReservationDTO result = reservationService.findByReservationNumber("OVR-2026-000001");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getReservationNumber()).isEqualTo("OVR-2026-000001");
        assertThat(result.getGuestName()).isEqualTo("John Smith");
    }

    // ═══════════════════════════════════════════════════════════
    // RST-05: Find by reservation number – not found
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: RST-05
     * Description: Finding a non-existent reservation number throws exception.
     * Preconditions: No reservation exists with the given number.
     * Steps: 1) Mock repository to return empty. 2) Call findByReservationNumber.
     * Expected Result: Throws ReservationNotFoundException.
     * Actual Result: Exception thrown — PASS.
     */
    @Test
    @DisplayName("RST-05: findByReservationNumber with non-existent number throws exception")
    void findByReservationNumber_NotFound_ThrowsNotFoundException() {
        // Arrange
        when(reservationRepository.findByReservationNumber("INVALID"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reservationService.findByReservationNumber("INVALID"))
                .isInstanceOf(ReservationNotFoundException.class);
    }

    // ═══════════════════════════════════════════════════════════
    // RST-06: Cancel reservation
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: RST-06
     * Description: Cancelling a PENDING reservation updates its status to CANCELLED.
     * Preconditions: Reservation exists with PENDING status.
     * Steps: 1) Mock repository to return PENDING reservation. 2) Call cancelReservation.
     * Expected Result: Reservation status is set to CANCELLED and saved.
     * Actual Result: Status updated to CANCELLED — PASS.
     */
    @Test
    @DisplayName("RST-06: cancelReservation with valid ID updates status to CANCELLED")
    void cancelReservation_ValidId_UpdatesStatus() {
        // Arrange
        testReservation.setStatus(ReservationStatus.PENDING);
        when(reservationRepository.findById("res-001")).thenReturn(Optional.of(testReservation));

        // Act
        reservationService.cancelReservation("res-001");

        // Assert
        assertThat(testReservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        verify(reservationRepository).save(testReservation);
    }

    // ═══════════════════════════════════════════════════════════
    // RST-07: Check-in changes status
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: RST-07
     * Description: Checking in a CONFIRMED reservation changes status to CHECKED_IN.
     * Preconditions: Reservation in CONFIRMED status.
     * Steps: 1) Mock repository. 2) Call checkIn. 3) Verify status and event.
     * Expected Result: Status changed to CHECKED_IN, CheckInEvent published.
     * Actual Result: Status updated and event published — PASS.
     */
    @Test
    @DisplayName("RST-07: checkIn with CONFIRMED reservation updates status to CHECKED_IN")
    void checkIn_ValidReservation_UpdatesStatusAndRoom() {
        // Arrange
        testReservation.setStatus(ReservationStatus.CONFIRMED);
        when(reservationRepository.findByReservationNumber("OVR-2026-000001"))
                .thenReturn(Optional.of(testReservation));

        Reservation saved = Reservation.builder()
                .id("res-001")
                .reservationNumber("OVR-2026-000001")
                .status(ReservationStatus.CHECKED_IN)
                .guestName("John Smith")
                .build();
        when(reservationRepository.save(any(Reservation.class))).thenReturn(saved);

        ReservationDTO checkedInDTO = ReservationDTO.builder()
                .id("res-001")
                .reservationNumber("OVR-2026-000001")
                .status(ReservationStatus.CHECKED_IN)
                .build();
        when(reservationMapper.toDTO(any(Reservation.class))).thenReturn(checkedInDTO);

        // Act
        ReservationDTO result = reservationService.checkIn("OVR-2026-000001");

        // Assert
        assertThat(result.getStatus()).isEqualTo(ReservationStatus.CHECKED_IN);
        verify(reservationRepository).save(any(Reservation.class));
        verify(eventPublisher).publishEvent(any());
    }

    // ═══════════════════════════════════════════════════════════
    // RST-08: Generate unique reservation numbers
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: RST-08
     * Description: ReservationNumberGenerator generates unique sequential numbers.
     * Preconditions: Generator is initialized.
     * Steps: 1) Create a real generator. 2) Generate multiple numbers. 3) Check uniqueness.
     * Expected Result: All generated numbers are unique and follow the format OVR-YYYY-NNNNNN.
     * Actual Result: All numbers unique with correct format — PASS.
     */
    @Test
    @DisplayName("RST-08: ReservationNumberGenerator produces unique numbers")
    void generateReservationNumber_ReturnsUniqueNumbers() {
        // Arrange — use real generator with a mock repository (returns 0 by default)
        ReservationNumberGenerator generator = new ReservationNumberGenerator(reservationRepository);

        // Act
        String number1 = generator.generateNext();
        String number2 = generator.generateNext();
        String number3 = generator.generateNext();

        // Assert – all unique
        assertThat(number1).isNotEqualTo(number2);
        assertThat(number2).isNotEqualTo(number3);
        assertThat(number1).isNotEqualTo(number3);

        // Assert – format OVR-YYYY-NNNNNN
        assertThat(number1).matches("OVR-\\d{4}-\\d{6}");
        assertThat(number2).matches("OVR-\\d{4}-\\d{6}");
        assertThat(number3).matches("OVR-\\d{4}-\\d{6}");

        // Assert – sequential
        assertThat(number1).endsWith("000001");
        assertThat(number2).endsWith("000002");
        assertThat(number3).endsWith("000003");
    }
}
