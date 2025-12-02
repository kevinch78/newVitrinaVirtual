package com.vitrina.vitrinaVirtual.domain.service.notification;

import com.vitrina.vitrinaVirtual.domain.dto.NotificationDto;
import com.vitrina.vitrinaVirtual.domain.repository.NotificationRepository;
import com.vitrina.vitrinaVirtual.infraestructura.entity.NotificationType;
import com.vitrina.vitrinaVirtual.infraestructura.entity.RecipientType;
import com.vitrina.vitrinaVirtual.infraestructura.entity.RelatedEntityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación profesional del servicio de notificaciones
 * Modo PRO activado 🚀
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public NotificationDto create(NotificationDto dto) {
        log.info("📨 Creando notificación: {} para {} ID={}",
                dto.getType(), dto.getRecipientType(), dto.getRecipientId());
        return notificationRepository.save(dto);
    }

    @Override
    @Transactional
    public NotificationDto createSimple(Long recipientId, RecipientType recipientType,
            String title, String message, NotificationType type) {
        NotificationDto dto = NotificationDto.builder()
                .recipientId(recipientId)
                .recipientType(recipientType)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .priority(5)
                .relatedEntityType(RelatedEntityType.NONE)
                .build();

        return create(dto);
    }

    @Override
    @Transactional
    public NotificationDto createWithRelatedEntity(Long recipientId, RecipientType recipientType,
            String title, String message, NotificationType type,
            Long relatedEntityId, RelatedEntityType relatedEntityType) {
        NotificationDto dto = NotificationDto.builder()
                .recipientId(recipientId)
                .recipientType(recipientType)
                .title(title)
                .message(message)
                .type(type)
                .relatedEntityId(relatedEntityId)
                .relatedEntityType(relatedEntityType)
                .isRead(false)
                .priority(5)
                .build();

        return create(dto);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationDto getById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getByClient(Long clientId) {
        log.debug("📥 Obteniendo todas las notificaciones del cliente {}", clientId);
        return notificationRepository.findByRecipient(clientId, RecipientType.CLIENT);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getUnreadByClient(Long clientId) {
        log.debug("🔔 Obteniendo notificaciones NO LEÍDAS del cliente {}", clientId);
        return notificationRepository.findUnreadByRecipient(clientId, RecipientType.CLIENT);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnreadByClient(Long clientId) {
        return notificationRepository.countUnreadByRecipient(clientId, RecipientType.CLIENT);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getByStore(Long storeId) {
        log.debug("📥 Obteniendo todas las notificaciones de la tienda {}", storeId);
        return notificationRepository.findByRecipient(storeId, RecipientType.STORE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getUnreadByStore(Long storeId) {
        log.debug("🔔 Obteniendo notificaciones NO LEÍDAS de la tienda {}", storeId);
        return notificationRepository.findUnreadByRecipient(storeId, RecipientType.STORE);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnreadByStore(Long storeId) {
        return notificationRepository.countUnreadByRecipient(storeId, RecipientType.STORE);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        log.debug("✓ Marcando notificación {} como leída", notificationId);
        notificationRepository.markAsRead(notificationId);
    }

    @Override
    @Transactional
    public int markAllAsReadForClient(Long clientId) {
        log.info("✓✓✓ Marcando todas las notificaciones del cliente {} como leídas", clientId);
        return notificationRepository.markAllAsRead(clientId, RecipientType.CLIENT);
    }

    @Override
    @Transactional
    public int markAllAsReadForStore(Long storeId) {
        log.info("✓✓✓ Marcando todas las notificaciones de la tienda {} como leídas", storeId);
        return notificationRepository.markAllAsRead(storeId, RecipientType.STORE);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("🗑️ Eliminando notificación {}", id);
        notificationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public int cleanupExpiredNotifications() {
        log.info("🧹 Iniciando limpieza de notificaciones expiradas...");
        return notificationRepository.deleteExpired();
    }

    // ========== MÉTODOS HELPER PARA EVENTOS COMUNES ==========

    @Override
    @Transactional
    public void notifyStoreNewReservation(Long storeId, Long reservationId, String clientName) {
        log.info("🏪 Notificando a tienda {} sobre nueva reserva #{}", storeId, reservationId);

        createWithRelatedEntity(
                storeId,
                RecipientType.STORE,
                "🆕 Nueva Reserva",
                String.format("El cliente %s ha realizado una nueva reserva. ¡Revísala pronto!", clientName),
                NotificationType.NEW_RESERVATION,
                reservationId,
                RelatedEntityType.RESERVATION);
    }

    @Override
    @Transactional
    public void notifyClientReservationAccepted(Long clientId, Long reservationId, String storeName) {
        log.info("👤 Notificando a cliente {} que su reserva fue aceptada", clientId);

        createWithRelatedEntity(
                clientId,
                RecipientType.CLIENT,
                "✅ ¡Reserva Aceptada!",
                String.format("¡Buenas noticias! %s ha aceptado tu reserva. Te contactarán pronto.", storeName),
                NotificationType.RESERVATION_ACCEPTED,
                reservationId,
                RelatedEntityType.RESERVATION);
    }

    @Override
    @Transactional
    public void notifyClientReservationRejected(Long clientId, Long reservationId, String storeName, String reason) {
        log.info("👤 Notificando a cliente {} que su reserva fue rechazada", clientId);

        String message = String.format(
                "Lo sentimos, %s no pudo procesar tu reserva.%s",
                storeName,
                reason != null && !reason.isEmpty() ? " Motivo: " + reason : "");

        createWithRelatedEntity(
                clientId,
                RecipientType.CLIENT,
                "❌ Reserva Rechazada",
                message,
                NotificationType.RESERVATION_REJECTED,
                reservationId,
                RelatedEntityType.RESERVATION);
    }

    @Override
    @Transactional
    public void notifyClientReservationReady(Long clientId, Long reservationId, String storeName) {
        log.info("👤 Notificando a cliente {} que su reserva está lista", clientId);

        createWithRelatedEntity(
                clientId,
                RecipientType.CLIENT,
                "📦 ¡Reserva Lista!",
                String.format("Tu reserva en %s está lista para recoger. ¡Te esperamos!", storeName),
                NotificationType.RESERVATION_READY,
                reservationId,
                RelatedEntityType.RESERVATION);
    }

    @Override
    @Transactional
    public void notifyStoreReservationCancelled(Long storeId, Long reservationId, String clientName) {
        log.info("🏪 Notificando a tienda {} que reserva fue cancelada", storeId);

        createWithRelatedEntity(
                storeId,
                RecipientType.STORE,
                "🚫 Reserva Cancelada",
                String.format("El cliente %s ha cancelado su reserva.", clientName),
                NotificationType.RESERVATION_CANCELLED,
                reservationId,
                RelatedEntityType.RESERVATION);
    }

    @Override
    @Transactional
    public void notifyStoreLowStock(Long storeId, Long productId, String productName, int currentStock) {
        log.warn("⚠️ Stock bajo de producto {} en tienda {}", productName, storeId);

        createWithRelatedEntity(
                storeId,
                RecipientType.STORE,
                "⚠️ Stock Bajo",
                String.format("El producto '%s' tiene solo %d unidades disponibles. Considera reabastecer.",
                        productName, currentStock),
                NotificationType.LOW_STOCK,
                productId,
                RelatedEntityType.PRODUCT);
    }
}
