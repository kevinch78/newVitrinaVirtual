package com.vitrina.vitrinaVirtual.infraestructura.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vitrina.vitrinaVirtual.domain.dto.StoreNotificationDto;
import com.vitrina.vitrinaVirtual.infraestructura.entity.StoreNotification;

@Mapper(componentModel = "spring")
public interface StoreNotificationMapper {

    @Mapping(target = "reservationItemId", source = "reservationItem.id")
    StoreNotificationDto toDto(StoreNotification entity);

    @Mapping(target = "reservationItem", ignore = true)
    StoreNotification toEntity(StoreNotificationDto dto);
}
