package com.vention.agroex.util.mapper;

import com.vention.agroex.dto.Notification;
import com.vention.agroex.entity.NotificationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationMapper {

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "lot.id", source = "lotId")
    NotificationEntity toEntity(Notification notification);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "lotId", source = "lot.id")
    Notification toDTO(NotificationEntity notificationEntity);

    List<NotificationEntity> toEntities(List<Notification> DTOs);

    List<Notification> toDTOs(List<NotificationEntity> notifications);
}
