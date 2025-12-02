package com.vitrina.vitrinaVirtual.domain.repository;

import java.util.List;
import java.util.Optional;

import com.vitrina.vitrinaVirtual.domain.dto.ReservationDto;

public interface ReservationRepository {
    ReservationDto save(ReservationDto dto);
    Optional<ReservationDto> findById(Long id);
    List<ReservationDto> findByClientId(Long clientId);
    List<ReservationDto> findByStoreId(Long storeId);
    List<ReservationDto> findByStatus(String status);
    List<ReservationDto> findAll();
    void deleteById(Long id);
}
