package com.vitrina.vitrinaVirtual.infraestructura.repositories;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.vitrina.vitrinaVirtual.domain.dto.ReservationItemDto;
import com.vitrina.vitrinaVirtual.domain.repository.ReservationItemRepository;
import com.vitrina.vitrinaVirtual.infraestructura.crud_interface.ReservationItemCrudRepository;
import com.vitrina.vitrinaVirtual.infraestructura.entity.ReservationItem;
import com.vitrina.vitrinaVirtual.infraestructura.mapper.ReservationItemMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationItemRepositoryImpl implements ReservationItemRepository {

    private final ReservationItemCrudRepository crud;
    private final ReservationItemMapper mapper;

    @Override
    public ReservationItemDto save(ReservationItemDto dto) {
        ReservationItem entity = mapper.toEntity(dto);
        return mapper.toDto(crud.save(entity));
    }

    @Override
    public Optional<ReservationItemDto> findById(Long id) {
        return crud.findById(id).map(mapper::toDto);
    }

    @Override
    public List<ReservationItemDto> findByReservationId(Long reservationId) {
        return crud.findByReservationId(reservationId)
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ReservationItemDto> findByStoreId(Long storeId) {
        return crud.findByStoreId(storeId)
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ReservationItemDto> findByStoreIdAndStatus(Long storeId, String status) {
        return crud.findByStoreIdAndStatus(storeId, status)
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        crud.deleteById(id);
    }
}
