package com.vitrina.vitrinaVirtual.domain.repository;

import com.vitrina.vitrinaVirtual.domain.dto.NotificationDto;
import com.vitrina.vitrinaVirtual.infraestructura.entity.NotificationType;
import com.vitrina.vitrinaVirtual.infraestructura.entity.RecipientType;
import com.vitrina.vitrinaVirtual.infraestructura.entity.RelatedEntityType;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz de repositorio de dominio para Notifications
 */
public interface NotificationRepository {

    /**
     * Guardar una notificación
     */
    NotificationDto save(NotificationDto dto);

    /**
     * Buscar notificación por ID
     */
    Optional<NotificationDto> findById(Long id);

    /**
     * Obtener todas las notificaciones de un destinatario
     */
    List<NotificationDto> findByRecipient(Long recipientId, RecipientType recipientType);

    /**
     * Obtener solo notificaciones no leídas de un destinatario
     */
    List<NotificationDto> findUnreadByRecipient(Long recipientId, RecipientType recipientType);

    /**
     * Contar notificaciones no leídas
     */
    long countUnreadByRecipient(Long recipientId, RecipientType recipientType);

    /**
     * Obtener notificaciones por tipo
     */
    List<NotificationDto> findByType(Long recipientId, RecipientType recipientType, NotificationType type);

    /**
     * Marcar como leída
     */
    void markAsRead(Long notificationId);

    /**
     * Marcar todas como leídas para un destinatario
     */
    int markAllAsRead(Long recipientId, RecipientType recipientType);

    /**
     * Eliminar una notificación
     */
    void deleteById(Long id);

    /**
     * Eliminar notificaciones expiradas
     */
    int deleteExpired();

    /**
     * Obtener notificaciones relacionadas con una entidad
     */
    List<NotificationDto> findByRelatedEntity(Long relatedEntityId, RelatedEntityType relatedEntityType);
}
