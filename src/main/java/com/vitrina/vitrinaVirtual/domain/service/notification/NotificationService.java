package com.vitrina.vitrinaVirtual.domain.service.notification;

import com.vitrina.vitrinaVirtual.domain.dto.NotificationDto;
import com.vitrina.vitrinaVirtual.infraestructura.entity.NotificationType;
import com.vitrina.vitrinaVirtual.infraestructura.entity.RecipientType;
import com.vitrina.vitrinaVirtual.infraestructura.entity.RelatedEntityType;

import java.util.List;

/**
 * Servicio de negocio para gestión de notificaciones
 */
public interface NotificationService {

    /**
     * Crear una nueva notificación
     */
    NotificationDto create(NotificationDto dto);

    /**
     * Crear notificación con builder simplificado
     */
    NotificationDto createSimple(Long recipientId, RecipientType recipientType,
            String title, String message, NotificationType type);

    /**
     * Crear notificación con entidad relacionada
     */
    NotificationDto createWithRelatedEntity(Long recipientId, RecipientType recipientType,
            String title, String message, NotificationType type,
            Long relatedEntityId, RelatedEntityType relatedEntityType);

    /**
     * Obtener notificación por ID
     */
    NotificationDto getById(Long id);

    /**
     * Obtener todas las notificaciones de un cliente
     */
    List<NotificationDto> getByClient(Long clientId);

    /**
     * Obtener notificaciones no leídas de un cliente
     */
    List<NotificationDto> getUnreadByClient(Long clientId);

    /**
     * Contar notificaciones no leídas de un cliente
     */
    long countUnreadByClient(Long clientId);

    /**
     * Obtener todas las notificaciones de una tienda
     */
    List<NotificationDto> getByStore(Long storeId);

    /**
     * Obtener notificaciones no leídas de una tienda
     */
    List<NotificationDto> getUnreadByStore(Long storeId);

    /**
     * Contar notificaciones no leídas de una tienda
     */
    long countUnreadByStore(Long storeId);

    /**
     * Marcar una notificación como leída
     */
    void markAsRead(Long notificationId);

    /**
     * Marcar todas las notificaciones de un cliente como leídas
     */
    int markAllAsReadForClient(Long clientId);

    /**
     * Marcar todas las notificaciones de una tienda como leídas
     */
    int markAllAsReadForStore(Long storeId);

    /**
     * Eliminar una notificación
     */
    void delete(Long id);

    /**
     * Limpiar notificaciones expiradas (tarea programada)
     */
    int cleanupExpiredNotifications();

    // ========== MÉTODOS HELPER PARA EVENTOS COMUNES ==========

    /**
     * Notificar a la tienda sobre nueva reserva
     */
    void notifyStoreNewReservation(Long storeId, Long reservationId, String clientName);

    /**
     * Notificar al cliente que su reserva fue aceptada
     */
    void notifyClientReservationAccepted(Long clientId, Long reservationId, String storeName);

    /**
     * Notificar al cliente que su reserva fue rechazada
     */
    void notifyClientReservationRejected(Long clientId, Long reservationId, String storeName, String reason);

    /**
     * Notificar al cliente que su reserva está lista
     */
    void notifyClientReservationReady(Long clientId, Long reservationId, String storeName);

    /**
     * Notificar a la tienda que una reserva fue cancelada
     */
    void notifyStoreReservationCancelled(Long storeId, Long reservationId, String clientName);

    /**
     * Notificar a la tienda sobre stock bajo
     */
    void notifyStoreLowStock(Long storeId, Long productId, String productName, int currentStock);
}
