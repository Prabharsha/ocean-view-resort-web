package com.oceanview.resort.dto;

import com.oceanview.resort.model.enums.ReservationStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Reservation entity.
 * Used for transferring reservation data between layers.
 *
 * <p><b>Design Pattern: Builder Pattern (via Lombok @Builder)</b></p>
 * <p>The Builder Pattern separates the construction of a complex object from
 * its representation, allowing the same construction process to create different
 * representations. Lombok's {@code @Builder} annotation generates a fluent builder
 * API at compile time.</p>
 *
 * <h3>Usage example:</h3>
 * <pre>{@code
 * ReservationDTO dto = ReservationDTO.builder()
 *     .customerId("cust-001")
 *     .roomId("room-101")
 *     .guestName("John Smith")
 *     .checkInDate(LocalDate.of(2026, 6, 1))
 *     .checkOutDate(LocalDate.of(2026, 6, 5))
 *     .numGuests(2)
 *     .status(ReservationStatus.PENDING)
 *     .build();
 * }</pre>
 *
 * <h3>Benefits:</h3>
 * <ul>
 *   <li>Readable object creation with named parameters.</li>
 *   <li>Immutable-friendly: build once, use everywhere.</li>
 *   <li>Avoids telescoping constructors with many parameters.</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {

    private String id;
    private String reservationNumber;
    private String customerId;
    private String customerName;
    private String roomId;
    private String staffId;
    private String billId;
    private String guestName;
    private String guestAddress;
    private String guestContact;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numGuests;
    private String specialRequests;
    private ReservationStatus status;
    private String roomNumber;
    private String roomType;
    private LocalDateTime createdAt;
}
