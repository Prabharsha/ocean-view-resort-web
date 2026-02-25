package com.oceanview.resort.dto;

import com.oceanview.resort.model.enums.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Payment entity.
 * Used for transferring payment data between layers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {

    private String id;
    private String billId;
    private String reservationId;
    private BigDecimal amountPaid;
    private PaymentMethod paymentMethod;
    private String transactionReference;
    private LocalDateTime paymentDate;
    private String processedBy;
    private String notes;
}
