package com.oceanview.resort.controller;

import com.oceanview.resort.dto.ReservationDTO;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.enums.ReservationStatus;
import com.oceanview.resort.model.enums.RoomType;
import com.oceanview.resort.service.interfaces.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for reservation management operations.
 *
 * <p><b>Design Pattern: MVC (Model-View-Controller) Pattern</b></p>
 * <p>The MVC Pattern separates an application into three interconnected layers:</p>
 * <ul>
 *   <li><b>Model:</b> Domain entities ({@code Reservation}, {@code Room}) and DTOs
 *       ({@code ReservationDTO}) — represent the data and business rules.</li>
 *   <li><b>View:</b> Thymeleaf templates (HTML) and JSON responses — present data
 *       to the user.</li>
 *   <li><b>Controller:</b> This class — handles HTTP requests, invokes the service
 *       layer, and returns appropriate responses. It does NOT contain business
 *       logic (that lives in the Service Layer).</li>
 * </ul>
 *
 * <h3>Request flow in this system:</h3>
 * <pre>
 *   HTTP Request → Controller (this class)
 *                     ↓ delegates to
 *                  Service Layer (ReservationService)
 *                     ↓ accesses
 *                  Repository Layer (ReservationRepository)
 *                     ↓ interacts with
 *                  Database (MySQL)
 * </pre>
 *
 * <p>Handles CRUD operations, check-in, check-out, and PDF generation
 * for reservations.</p>
 */
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reservations", description = "Reservation management endpoints")
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * Creates a new reservation.
     */
    @PostMapping
    @Operation(summary = "Create Reservation", description = "Create a new room reservation")
    public ResponseEntity<ReservationDTO> createReservation(
            @Valid @RequestBody ReservationDTO reservationDTO) {
        log.info("Creating reservation for customer: {}", reservationDTO.getCustomerId());
        ReservationDTO created = reservationService.createReservation(reservationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Returns all reservations (STAFF / MANAGER).
     */
    @GetMapping
    @Operation(summary = "List All Reservations", description = "Get all reservations (staff/manager only)")
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<ReservationDTO> reservations = reservationService.findAllReservations();
        return ResponseEntity.ok(reservations);
    }

    /**
     * Returns a reservation by its UUID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get Reservation", description = "Get a reservation by its ID")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable String id) {
        // Use reservation number lookup since service uses it
        ReservationDTO reservation = reservationService.findByReservationNumber(id);
        return ResponseEntity.ok(reservation);
    }

    /**
     * Returns a reservation by its reservation number.
     */
    @GetMapping("/number/{reservationNumber}")
    @Operation(summary = "Get by Number", description = "Get a reservation by reservation number")
    public ResponseEntity<ReservationDTO> getByReservationNumber(
            @PathVariable String reservationNumber) {
        ReservationDTO reservation = reservationService.findByReservationNumber(reservationNumber);
        return ResponseEntity.ok(reservation);
    }

    /**
     * Returns all reservations for a specific customer.
     */
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get Customer Reservations",
            description = "Get all reservations for a specific customer")
    public ResponseEntity<List<ReservationDTO>> getCustomerReservations(
            @PathVariable String customerId) {
        List<ReservationDTO> reservations = reservationService.findByCustomerId(customerId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Updates a reservation's status.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update Reservation Status", description = "Update reservation status")
    public ResponseEntity<ReservationDTO> updateReservation(
            @PathVariable String id,
            @RequestParam ReservationStatus status) {
        ReservationDTO updated = reservationService.updateReservationStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    /**
     * Cancels a reservation.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel Reservation", description = "Cancel a reservation")
    public ResponseEntity<Void> cancelReservation(@PathVariable String id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Checks in a guest.
     */
    @PutMapping("/{reservationNumber}/checkin")
    @Operation(summary = "Check In", description = "Check in a guest")
    public ResponseEntity<ReservationDTO> checkIn(@PathVariable String reservationNumber) {
        ReservationDTO checkedIn = reservationService.checkIn(reservationNumber);
        return ResponseEntity.ok(checkedIn);
    }

    /**
     * Checks out a guest.
     */
    @PutMapping("/{reservationNumber}/checkout")
    @Operation(summary = "Check Out", description = "Check out a guest")
    public ResponseEntity<ReservationDTO> checkOut(@PathVariable String reservationNumber) {
        ReservationDTO checkedOut = reservationService.checkOut(reservationNumber);
        return ResponseEntity.ok(checkedOut);
    }

    /**
     * Downloads a reservation confirmation as PDF.
     */
    @GetMapping("/{id}/pdf")
    @Operation(summary = "Download PDF", description = "Download reservation confirmation PDF")
    public ResponseEntity<byte[]> downloadPDF(@PathVariable String id) {
        byte[] pdf = reservationService.generateReservationConfirmationPDF(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                "reservation-confirmation-" + id + ".pdf");
        headers.setContentLength(pdf.length);

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    /**
     * Finds available rooms for the given date range and optional type.
     */
    @GetMapping("/available-rooms")
    @Operation(summary = "Available Rooms", description = "Find available rooms for date range")
    public ResponseEntity<List<Room>> getAvailableRooms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(required = false) RoomType type) {
        List<Room> rooms = reservationService.findAvailableRooms(checkIn, checkOut, type);
        return ResponseEntity.ok(rooms);
    }
}
