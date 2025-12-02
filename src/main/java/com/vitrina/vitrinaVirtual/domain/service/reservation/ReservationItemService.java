package com.vitrina.vitrinaVirtual.domain.service.reservation;

import java.util.List;
import java.util.Optional;

import com.vitrina.vitrinaVirtual.domain.dto.ReservationItemDto;

public interface ReservationItemService {

    ReservationItemDto create(ReservationItemDto dto);

    Optional<ReservationItemDto> getById(Long id);

    List<ReservationItemDto> getByReservation(Long reservationId);

    List<ReservationItemDto> getByStore(Long storeId);

    List<ReservationItemDto> getByStoreAndStatus(Long storeId, String status);

    ReservationItemDto update(ReservationItemDto dto);

    void delete(Long id);
}
