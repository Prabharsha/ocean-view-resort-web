package com.oceanview.resort.dto;

import com.oceanview.resort.model.enums.RoomType;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO carrying advanced room search criteria.
 *
 * <p>Supports filtering by room type, price range, minimum capacity,
 * and result sorting. All fields are optional — when a field is
 * {@code null} the corresponding filter is not applied.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomSearchCriteria {

    /** Filter by room type (e.g. STANDARD, DELUXE, SUITE, PENTHOUSE). */
    private RoomType roomType;

    /** Minimum price per night (inclusive). */
    private BigDecimal minPrice;

    /** Maximum price per night (inclusive). */
    private BigDecimal maxPrice;

    /** Minimum guest capacity required. */
    private Integer minCapacity;

    /** Floor number filter. */
    private Integer floorNumber;

    /** Only include available rooms. */
    @Builder.Default
    private boolean availableOnly = true;

    /** Sort field: "ratePerNight", "capacity", "roomNumber", "floorNumber". */
    @Builder.Default
    private String sortBy = "ratePerNight";

    /** Sort direction: "ASC" or "DESC". */
    @Builder.Default
    private String sortDirection = "ASC";
}
