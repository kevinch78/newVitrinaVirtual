package com.vitrina.vitrinaVirtual.domain.repository;

import java.util.List;
import java.util.Optional;

import com.vitrina.vitrinaVirtual.domain.dto.ReservationItemDto;

public interface ReservationItemRepository {

    ReservationItemDto save(ReservationItemDto dto);

    Optional<ReservationItemDto> findById(Long id);

    List<ReservationItemDto> findByReservationId(Long reservationId);

    List<ReservationItemDto> findByStoreId(Long storeId);

    List<ReservationItemDto> findByStoreIdAndStatus(Long storeId, String status);

    void delete(Long id);
}
