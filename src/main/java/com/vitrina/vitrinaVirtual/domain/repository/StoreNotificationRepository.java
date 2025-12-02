package com.vitrina.vitrinaVirtual.domain.repository;

import java.util.List;
import java.util.Optional;

import com.vitrina.vitrinaVirtual.domain.dto.StoreNotificationDto;


public interface StoreNotificationRepository {

    StoreNotificationDto save(StoreNotificationDto notif);

    Optional<StoreNotificationDto> findById(Long id);

    List<StoreNotificationDto> findByStoreId(Long storeId);

    List<StoreNotificationDto> findUnreadByStoreId(Long storeId);

    void markAsRead(Long id);

    void deleteById(Long id);
}