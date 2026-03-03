package com.oceanview.resort.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private int numNights;
    private BigDecimal roomRate;
    private BigDecimal subtotal;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private LocalDateTime generatedAt;
}
