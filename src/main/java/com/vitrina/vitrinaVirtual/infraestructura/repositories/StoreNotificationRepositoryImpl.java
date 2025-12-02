package com.vitrina.vitrinaVirtual.infraestructura.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.vitrina.vitrinaVirtual.domain.dto.StoreNotificationDto;
import com.vitrina.vitrinaVirtual.domain.repository.StoreNotificationRepository;
import com.vitrina.vitrinaVirtual.infraestructura.crud_interface.StoreNotificationCrudRepository;
import com.vitrina.vitrinaVirtual.infraestructura.entity.StoreNotification;
import com.vitrina.vitrinaVirtual.infraestructura.mapper.StoreNotificationMapper;

import lombok.RequiredArgsConstructor;


@Repository
@RequiredArgsConstructor
public class StoreNotificationRepositoryImpl implements StoreNotificationRepository {

    private final StoreNotificationCrudRepository jpa;
    private final StoreNotificationMapper mapper;

    @Override
    public StoreNotificationDto save(StoreNotificationDto dto) {
        StoreNotification entity = mapper.toEntity(dto);
        StoreNotification saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public Optional<StoreNotificationDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<StoreNotificationDto> findByStoreId(Long storeId) {
        return jpa.findByStoreId(storeId)
                  .stream()
                  .map(mapper::toDto)
                  .toList();
    }

    @Override
    public List<StoreNotificationDto> findUnreadByStoreId(Long storeId) {
        return jpa.findUnreadByStoreId(storeId)
                  .stream()
                  .map(mapper::toDto)
                  .toList();
    }

    @Override
    public void markAsRead(Long id) {
        jpa.markAsRead(id);
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }
}