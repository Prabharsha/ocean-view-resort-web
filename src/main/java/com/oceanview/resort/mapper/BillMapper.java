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

    @Mapping(source = "reservation.id",                target = "reservationId")
    @Mapping(source = "reservation.reservationNumber", target = "reservationNumber")
    @Mapping(source = "reservation.guestName",         target = "guestName")
    @Mapping(source = "reservation.guestContact",      target = "guestPhone")
    @Mapping(source = "reservation.room.roomNumber",   target = "roomNumber")
    @Mapping(target = "roomType",  expression = "java(bill.getReservation() != null && bill.getReservation().getRoom() != null ? bill.getReservation().getRoom().getRoomType().getDisplayName() : null)")
    @Mapping(source = "reservation.checkInDate",       target = "checkInDate")
    @Mapping(source = "reservation.checkOutDate",      target = "checkOutDate")
    @Mapping(target = "guestEmail",                    ignore = true)
    @Mapping(target = "payments",                      ignore = true)
    BillDTO toDTO(Bill bill);

    @Mapping(target = "reservation", ignore = true)
    @Mapping(target = "payments",    ignore = true)
    Bill toEntity(BillDTO dto);
}
