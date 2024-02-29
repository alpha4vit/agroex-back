package com.vention.agroex.util.mapper;

import com.vention.agroex.dto.User;
import com.vention.agroex.dto.UserRegistration;
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

    @Mapping(target = "creationDate", expression = "java(userEntity.getCreationDate().atZone(userEntity.getTimeZone()))")
    User toDTO(UserEntity userEntity);

    List<UserEntity> toEntities(List<User> dtos);

    List<User> toDtos(List<UserEntity> userEntities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    UserEntity update(@MappingTarget UserEntity target, UserEntity source);

    @Mapping(target = "id", source = "user.sub")
    @Mapping(target = "username", source = "user.name")
    @Mapping(target = "timeZone", expression = "java(ZoneId.of(user.getZoneinfo()))")
    UserEntity toEntity(UserRegistration user);

}
