package com.vitrina.vitrinaVirtual.domain.service.reservation;

import java.util.List;
import java.util.Optional;

import com.vitrina.vitrinaVirtual.domain.dto.StoreNotificationDto;


public interface StoreNotificationService {

    StoreNotificationDto create(StoreNotificationDto dto);

    Optional<StoreNotificationDto> getById(Long id);

    List<StoreNotificationDto> getByStore(Long storeId);

    List<StoreNotificationDto> getUnreadByStore(Long storeId);

    void markAsRead(Long id);

    void delete(Long id);
}
