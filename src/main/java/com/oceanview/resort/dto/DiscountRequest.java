package com.oceanview.resort.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/**
 * Request DTO for applying a discount to a bill.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountRequest {

    @NotNull(message = "Discount percentage is required")
    @DecimalMin(value = "0.01", message = "Discount must be at least 0.01%")
    private BigDecimal discountPercent;
}
