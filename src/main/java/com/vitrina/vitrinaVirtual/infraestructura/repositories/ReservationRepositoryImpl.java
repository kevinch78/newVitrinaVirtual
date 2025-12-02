package com.vitrina.vitrinaVirtual.infraestructura.repositories;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.vitrina.vitrinaVirtual.domain.dto.ReservationDto;
import com.vitrina.vitrinaVirtual.domain.repository.ReservationRepository;
import com.vitrina.vitrinaVirtual.infraestructura.crud_interface.ReservationCrudRepository;
import com.vitrina.vitrinaVirtual.infraestructura.entity.Reservation;
import com.vitrina.vitrinaVirtual.infraestructura.mapper.ReservationMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {

    private final ReservationCrudRepository jpa;
    private final ReservationMapper mapper;

    @Override
    public ReservationDto save(ReservationDto dto) {
        // Si tiene ID, es un UPDATE - solo actualizar campos simples
        if (dto.getId() != null) {
            Reservation existing = jpa.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + dto.getId()));
            
            // Actualizar SOLO los campos que pueden cambiar
            // NO tocar los items - conservar los existentes de la DB
            existing.setStatus(dto.getStatus());
            if (dto.getTotalPrice() != null) {
                existing.setTotalPrice(dto.getTotalPrice());
            }
            
            Reservation saved = jpa.save(existing);
            return mapper.toDto(saved);
        } else {
            // Es un CREATE - usar el mapper normal
            Reservation entity = mapper.toEntity(dto);
            Reservation saved = jpa.save(entity);
            return mapper.toDto(saved);
        }
    }

    @Override
    public Optional<ReservationDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<ReservationDto> findByClientId(Long clientId) {
        return jpa.findByClientId(clientId)
                  .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ReservationDto> findByStoreId(Long storeId) {
        return jpa.findByStoreId(storeId)
                  .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ReservationDto> findByStatus(String status) {
        return jpa.findByStatus(status)
                  .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ReservationDto> findAll() {
        return jpa.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }
}