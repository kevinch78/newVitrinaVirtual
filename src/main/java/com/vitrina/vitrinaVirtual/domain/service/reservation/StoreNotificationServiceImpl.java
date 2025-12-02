package com.vitrina.vitrinaVirtual.domain.service.reservation;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.vitrina.vitrinaVirtual.domain.dto.StoreNotificationDto;
import com.vitrina.vitrinaVirtual.domain.repository.StoreNotificationRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class StoreNotificationServiceImpl implements StoreNotificationService {

    private final StoreNotificationRepository repository;

    @Override
    public StoreNotificationDto create(StoreNotificationDto dto) {
        return repository.save(dto);
    }

    @Override
    public Optional<StoreNotificationDto> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<StoreNotificationDto> getByStore(Long storeId) {
        return repository.findByStoreId(storeId);
    }

    @Override
    public List<StoreNotificationDto> getUnreadByStore(Long storeId) {
        return repository.findUnreadByStoreId(storeId);
    }

    @Override
    public void markAsRead(Long id) {
        repository.markAsRead(id);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}