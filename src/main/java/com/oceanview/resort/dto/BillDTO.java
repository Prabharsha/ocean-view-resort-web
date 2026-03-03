package com.oceanview.resort.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for Bill entity.
 * Used for transferring bill data between layers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillDTO {

    private String id;
    private String reservationId;
    private String reservationNumber;

    // Guest / room info (denormalised for display)
    private String guestName;
    private String guestEmail;
    private String guestPhone;
    private String roomNumber;
    private String roomType;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private int numNights;
    private BigDecimal roomRate;
    private BigDecimal subtotal;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private LocalDateTime generatedAt;

    // Payments recorded against this bill
    private List<PaymentDTO> payments;
}
