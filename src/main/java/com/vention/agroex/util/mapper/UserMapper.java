package com.vention.agroex.util.mapper;

import com.vention.agroex.dto.UserDTO;
import com.vention.agroex.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserDTO userDTO);

    UserDTO toDTO(User user);

    List<User> toEntities(List<UserDTO> dtos);

    List<UserDTO> toDtos(List<User> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    User update(@MappingTarget User target, UserDTO source);

}
