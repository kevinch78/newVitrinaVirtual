package com.vitrina.vitrinaVirtual.domain.service.reservation;

import java.util.List;

import com.vitrina.vitrinaVirtual.domain.dto.ReservationDto;

public interface ReservationService {
    ReservationDto create(ReservationDto dto);
    ReservationDto getById(Long id);
    List<ReservationDto> getByClient(Long clientId);
    List<ReservationDto> getByStore(Long storeId);
    ReservationDto update(ReservationDto dto);
    void delete(Long id);
}

