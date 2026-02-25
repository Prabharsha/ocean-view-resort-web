package com.oceanview.resort.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.oceanview.resort.dto.ReservationDTO;
import com.oceanview.resort.exception.GlobalExceptionHandler;
import com.oceanview.resort.exception.ReservationNotFoundException;
import com.oceanview.resort.model.enums.ReservationStatus;
import com.oceanview.resort.service.interfaces.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link ReservationController} using MockMvc (standalone setup).
 *
 * <h3>Test Plan Document</h3>
 * <table>
 *   <tr><th>Test ID</th><th>Description</th><th>Preconditions</th><th>Steps</th><th>Expected</th><th>Status</th></tr>
 *   <tr><td>RCT-01</td><td>POST /api/reservations returns 201 Created</td>
 *       <td>Valid reservation DTO</td>
 *       <td>POST with valid JSON body</td><td>201 with created reservation</td><td>PASS</td></tr>
 *   <tr><td>RCT-02</td><td>GET /api/reservations returns 200 OK</td>
 *       <td>Reservations exist</td>
 *       <td>GET all reservations</td><td>200 with list of reservations</td><td>PASS</td></tr>
 *   <tr><td>RCT-03</td><td>GET /api/reservations/number/{num} returns 200</td>
 *       <td>Reservation exists</td>
 *       <td>GET by reservation number</td><td>200 with reservation DTO</td><td>PASS</td></tr>
 *   <tr><td>RCT-04</td><td>GET /api/reservations/{id} not found returns 404</td>
 *       <td>No reservation with given ID</td>
 *       <td>GET with invalid ID</td><td>404 Not Found</td><td>PASS</td></tr>
 *   <tr><td>RCT-05</td><td>DELETE /api/reservations/{id} returns 204</td>
 *       <td>Reservation in PENDING status</td>
 *       <td>DELETE reservation</td><td>204 No Content</td><td>PASS</td></tr>
 *   <tr><td>RCT-06</td><td>PUT /{reservationNumber}/checkin returns 200</td>
 *       <td>Reservation in CONFIRMED status</td>
 *       <td>PUT checkin</td><td>200 with CHECKED_IN status</td><td>PASS</td></tr>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private ReservationDTO sampleDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(reservationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        sampleDTO = ReservationDTO.builder()
                .id("res-001")
                .reservationNumber("OVR-2026-000001")
                .customerId("cust-001")
                .roomId("room-101")
                .guestName("John Smith")
                .guestContact("+1234567890")
                .checkInDate(LocalDate.of(2026, 6, 1))
                .checkOutDate(LocalDate.of(2026, 6, 4))
                .numGuests(2)
                .status(ReservationStatus.PENDING)
                .roomNumber("101")
                .roomType("DELUXE")
                .build();
    }

    // ═══════════════════════════════════════════════════════════
    // RCT-01: POST /api/reservations – 201 Created
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: RCT-01
     * Description: Creating a reservation returns 201 Created.
     * Preconditions: Valid request body, room available.
     * Steps: 1) Mock service. 2) POST JSON body.
     * Expected Result: 201 Created with ReservationDTO in response.
     * Actual Result: Reservation created — PASS.
     */
    @Test
    @DisplayName("RCT-01: POST /api/reservations returns 201 Created")
    void createReservation_ValidData_Returns201() throws Exception {
        // Arrange
        when(reservationService.createReservation(any(ReservationDTO.class)))
                .thenReturn(sampleDTO);

        ReservationDTO requestDTO = ReservationDTO.builder()
                .customerId("cust-001")
                .roomId("room-101")
                .guestName("John Smith")
                .guestContact("+1234567890")
                .checkInDate(LocalDate.of(2026, 6, 1))
                .checkOutDate(LocalDate.of(2026, 6, 4))
                .numGuests(2)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reservationNumber").value("OVR-2026-000001"))
                .andExpect(jsonPath("$.guestName").value("John Smith"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(reservationService).createReservation(any(ReservationDTO.class));
    }

    // ═══════════════════════════════════════════════════════════
    // RCT-02: GET /api/reservations – 200 OK
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: RCT-02
     * Description: Getting all reservations returns 200 OK with list.
     * Preconditions: Reservations exist in system.
     * Steps: 1) Mock service. 2) GET /api/reservations.
     * Expected Result: 200 OK with JSON array of reservations.
     * Actual Result: List returned — PASS.
     */
    @Test
    @DisplayName("RCT-02: GET /api/reservations returns 200 with list")
    void getAllReservations_Returns200() throws Exception {
        // Arrange
        when(reservationService.findAllReservations()).thenReturn(List.of(sampleDTO));

        // Act & Assert
        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reservationNumber").value("OVR-2026-000001"))
                .andExpect(jsonPath("$[0].guestName").value("John Smith"));
    }

    // ═══════════════════════════════════════════════════════════
    // RCT-03: GET by reservation number – 200 OK
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: RCT-03
     * Description: Getting reservation by number returns 200 OK.
     * Preconditions: Reservation with the given number exists.
     * Steps: 1) Mock service. 2) GET /api/reservations/number/{num}.
     * Expected Result: 200 OK with matching ReservationDTO.
     * Actual Result: DTO returned — PASS.
     */
    @Test
    @DisplayName("RCT-03: GET /api/reservations/number/{num} returns 200")
    void getByReservationNumber_Exists_Returns200() throws Exception {
        // Arrange
        when(reservationService.findByReservationNumber("OVR-2026-000001"))
                .thenReturn(sampleDTO);

        // Act & Assert
        mockMvc.perform(get("/api/reservations/number/OVR-2026-000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationNumber").value("OVR-2026-000001"))
                .andExpect(jsonPath("$.guestName").value("John Smith"));
    }

    // ═══════════════════════════════════════════════════════════
    // RCT-04: GET /api/reservations/{id} – 404 Not Found
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: RCT-04
     * Description: Getting a non-existent reservation returns 404.
     * Preconditions: No reservation with the given ID.
     * Steps: 1) Mock service to throw exception. 2) GET with invalid ID.
     * Expected Result: 404 Not Found.
     * Actual Result: 404 returned — PASS.
     */
    @Test
    @DisplayName("RCT-04: GET /api/reservations/{id} not found returns 404")
    void getReservationById_NotFound_Returns404() throws Exception {
        // Arrange: the controller calls findByReservationNumber for /{id}
        when(reservationService.findByReservationNumber("INVALID-ID"))
                .thenThrow(new ReservationNotFoundException("reservationNumber", "INVALID-ID"));

        // Act & Assert – GlobalExceptionHandler maps ReservationNotFoundException → 404
        mockMvc.perform(get("/api/reservations/INVALID-ID"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    // ═══════════════════════════════════════════════════════════
    // RCT-05: DELETE /api/reservations/{id} – 204 No Content
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: RCT-05
     * Description: Cancelling a reservation returns 204 No Content.
     * Preconditions: Reservation exists and is in PENDING status.
     * Steps: 1) Mock service (no exception). 2) DELETE /{id}.
     * Expected Result: 204 No Content.
     * Actual Result: 204 returned — PASS.
     */
    @Test
    @DisplayName("RCT-05: DELETE /api/reservations/{id} returns 204")
    void cancelReservation_Returns204() throws Exception {
        // Arrange
        doNothing().when(reservationService).cancelReservation("res-001");

        // Act & Assert
        mockMvc.perform(delete("/api/reservations/res-001"))
                .andExpect(status().isNoContent());

        verify(reservationService).cancelReservation("res-001");
    }

    // ═══════════════════════════════════════════════════════════
    // RCT-06: PUT /{reservationNumber}/checkin – 200 OK
    // ═══════════════════════════════════════════════════════════

    /**
     * Test ID: RCT-06
     * Description: Check-in endpoint returns updated reservation with CHECKED_IN status.
     * Preconditions: Reservation in CONFIRMED status.
     * Steps: 1) Mock service. 2) PUT /{reservationNumber}/checkin.
     * Expected Result: 200 OK with CHECKED_IN status.
     * Actual Result: Status updated — PASS.
     */
    @Test
    @DisplayName("RCT-06: PUT /{reservationNumber}/checkin returns 200 with CHECKED_IN")
    void checkIn_ValidReservation_Returns200() throws Exception {
        // Arrange
        ReservationDTO checkedInDTO = ReservationDTO.builder()
                .id("res-001")
                .reservationNumber("OVR-2026-000001")
                .status(ReservationStatus.CHECKED_IN)
                .guestName("John Smith")
                .build();
        when(reservationService.checkIn("OVR-2026-000001")).thenReturn(checkedInDTO);

        // Act & Assert
        mockMvc.perform(put("/api/reservations/OVR-2026-000001/checkin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CHECKED_IN"))
                .andExpect(jsonPath("$.reservationNumber").value("OVR-2026-000001"));
    }
}
