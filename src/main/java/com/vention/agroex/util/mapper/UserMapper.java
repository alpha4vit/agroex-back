package com.vention.agroex.util.mapper;

import com.vention.agroex.dto.User;
import com.vention.agroex.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring", imports = ZoneId.class)
public interface UserMapper {

    @Mapping(target = "creationDate", ignore = true)
    UserEntity toEntity(User user);

    @Mapping(target = "creationDate", expression = "java(userEntity.getCreationDate().atZone(userEntity.getZoneinfo()))")
    User toDTO(UserEntity userEntity);

    List<UserEntity> toEntities(List<User> dtos);

    List<User> toDtos(List<UserEntity> userEntities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    UserEntity update(@MappingTarget UserEntity target, UserEntity source);

}
