package com.vitrina.vitrinaVirtual.infraestructura.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.vitrina.vitrinaVirtual.domain.dto.ReservationItemDto;
import com.vitrina.vitrinaVirtual.infraestructura.entity.ReservationItem;

@Mapper(componentModel = "spring", uses = {ProductoMapper.class})
public interface ReservationItemMapper {

    @Mappings({
        @Mapping(target = "id", source = "id"),
        @Mapping(target = "productId", source = "producto.idProducto"),
        @Mapping(target = "storeId", source = "store.id"),
        @Mapping(target = "reservationId", source = "reservation.id"),
        @Mapping(target = "product", source = "producto")
    })
    ReservationItemDto toDto(ReservationItem entity);

    @Mappings({
        @Mapping(target = "producto", ignore = true),
        @Mapping(target = "store", ignore = true),
        @Mapping(target = "reservation", ignore = true)
    })
    ReservationItem toEntity(ReservationItemDto dto);
}