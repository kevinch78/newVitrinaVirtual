package com.vitrina.vitrinaVirtual.infraestructura.repositories;

import com.vitrina.vitrinaVirtual.domain.dto.NotificationDto;
import com.vitrina.vitrinaVirtual.domain.repository.NotificationRepository;
import com.vitrina.vitrinaVirtual.infraestructura.crud_interface.NotificationCrudRepository;
import com.vitrina.vitrinaVirtual.infraestructura.entity.Notification;
import com.vitrina.vitrinaVirtual.infraestructura.entity.NotificationType;
import com.vitrina.vitrinaVirtual.infraestructura.entity.RecipientType;
import com.vitrina.vitrinaVirtual.infraestructura.entity.RelatedEntityType;
import com.vitrina.vitrinaVirtual.infraestructura.mapper.NotificationMapperManual;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del repositorio de Notifications
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationCrudRepository jpa;
    private final NotificationMapperManual mapper;

    @Override
    @Transactional
    public NotificationDto save(NotificationDto dto) {
        log.debug("💾 Guardando notificación: {}", dto.getTitle());
        Notification entity = mapper.toEntity(dto);
        Notification saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NotificationDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> findByRecipient(Long recipientId, RecipientType recipientType) {
        log.debug("📥 Obteniendo notificaciones para {} ID={}", recipientType, recipientId);
        List<Notification> notifications = jpa.findByRecipientIdAndRecipientTypeOrderByCreatedAtDesc(
                recipientId, recipientType);
        return mapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> findUnreadByRecipient(Long recipientId, RecipientType recipientType) {
        log.debug("🔔 Obteniendo notificaciones NO LEÍDAS para {} ID={}", recipientType, recipientId);
        List<Notification> notifications = jpa.findByRecipientIdAndRecipientTypeAndIsReadFalseOrderByCreatedAtDesc(
                recipientId, recipientType);
        log.debug("✅ Encontradas {} notificaciones no leídas", notifications.size());
        return mapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnreadByRecipient(Long recipientId, RecipientType recipientType) {
        return jpa.countByRecipientIdAndRecipientTypeAndIsReadFalse(recipientId, recipientType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> findByType(Long recipientId, RecipientType recipientType, NotificationType type) {
        List<Notification> notifications = jpa.findByRecipientIdAndRecipientTypeAndTypeOrderByCreatedAtDesc(
                recipientId, recipientType, type);
        return mapper.toDtoList(notifications);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        log.debug("✓ Marcando notificación {} como leída", notificationId);
        jpa.findById(notificationId).ifPresent(notification -> {
            notification.markAsRead();
            jpa.save(notification);
        });
    }

    @Override
    @Transactional
    public int markAllAsRead(Long recipientId, RecipientType recipientType) {
        log.info("✓✓✓ Marcando TODAS las notificaciones como leídas para {} ID={}",
                recipientType, recipientId);
        int updated = jpa.markAllAsReadByRecipient(recipientId, recipientType, LocalDateTime.now());
        log.info("✅ {} notificaciones marcadas como leídas", updated);
        return updated;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("🗑️ Eliminando notificación {}", id);
        jpa.deleteById(id);
    }

    @Override
    @Transactional
    public int deleteExpired() {
        log.info("🧹 Limpiando notificaciones expiradas...");
        int deleted = jpa.deleteExpiredNotifications(LocalDateTime.now());
        log.info("✅ {} notificaciones expiradas eliminadas", deleted);
        return deleted;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> findByRelatedEntity(Long relatedEntityId, RelatedEntityType relatedEntityType) {
        List<Notification> notifications = jpa.findByRelatedEntityIdAndRelatedEntityType(
                relatedEntityId, relatedEntityType);
        return mapper.toDtoList(notifications);
    }
}
