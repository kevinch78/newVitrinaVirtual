package com.vitrina.vitrinaVirtual.infraestructura.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

import com.vitrina.vitrinaVirtual.domain.dto.ReservationDto;
import com.vitrina.vitrinaVirtual.infraestructura.crud_interface.AlmacenCrudRepository;
import com.vitrina.vitrinaVirtual.infraestructura.entity.Almacen;
import com.vitrina.vitrinaVirtual.infraestructura.entity.Reservation;

@Mapper(componentModel = "spring", uses = { ReservationItemMapper.class })
public abstract class ReservationMapper {

    @Autowired
    protected AlmacenCrudRepository almacenRepository;

    // DTO -> Entity: convertir storeId a objeto Almacen
    @Mappings({
        @Mapping(target = "store", expression = "java(mapStore(dto.getStoreId()))"),
        @Mapping(target = "items", source = "items")
    })
    public abstract Reservation toEntity(ReservationDto dto);

    // Entity -> DTO: convertir Almacen a storeId
    @Mappings({
        @Mapping(target = "storeId", source = "id"),
        @Mapping(target = "items", source = "items")
    })
    public abstract ReservationDto toDto(Reservation entity);

    // Método helper para mapear storeId -> Almacen
    protected Almacen mapStore(Long storeId) {
        if (storeId == null) {
            return null;
        }
        return almacenRepository.findById(storeId)
            .orElseThrow(() -> new RuntimeException("Tienda con ID " + storeId + " no encontrada"));
    }
}