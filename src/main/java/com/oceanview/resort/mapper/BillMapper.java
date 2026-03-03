package com.oceanview.resort.mapper;

import com.oceanview.resort.dto.BillDTO;
import com.oceanview.resort.model.Bill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for converting between Bill entity and BillDTO.
 */
@Mapper(componentModel = "spring")
public interface BillMapper {

    @Mapping(source = "reservation.id", target = "reservationId")
    @Mapping(source = "reservation.reservationNumber", target = "reservationNumber")
    BillDTO toDTO(Bill bill);

    @Mapping(target = "reservation", ignore = true)
    @Mapping(target = "payments", ignore = true)
    Bill toEntity(BillDTO dto);
}
