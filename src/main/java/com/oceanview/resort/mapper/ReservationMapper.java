package com.oceanview.resort.mapper;

import com.oceanview.resort.dto.ReservationDTO;
import com.oceanview.resort.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for converting between Reservation entity and ReservationDTO.
 */
@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "room.id", target = "roomId")
    @Mapping(source = "staff.id", target = "staffId")
    @Mapping(source = "room.roomNumber", target = "roomNumber")
    @Mapping(source = "room.roomType", target = "roomType")
    ReservationDTO toDTO(Reservation reservation);

    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "staff", ignore = true)
    @Mapping(target = "bill", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Reservation toEntity(ReservationDTO dto);
}
