package com.oceanview.resort.mapper;

import com.oceanview.resort.dto.UserDTO;
import com.oceanview.resort.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for converting between User entity and UserDTO.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "isActive", source = "active")
    UserDTO toDTO(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "isActive", source = "active")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserDTO dto);
}
