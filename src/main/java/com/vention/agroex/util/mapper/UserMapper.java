package com.vention.agroex.util.mapper;

import com.vention.agroex.dto.User;
import com.vention.agroex.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(User user);

    User toDTO(UserEntity userEntity);

    List<UserEntity> toEntities(List<User> dtos);

    List<User> toDtos(List<UserEntity> userEntities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "password", ignore = true)
    UserEntity update(@MappingTarget UserEntity target, UserEntity source);

}
