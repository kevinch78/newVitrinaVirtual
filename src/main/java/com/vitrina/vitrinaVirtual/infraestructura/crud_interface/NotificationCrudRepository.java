package com.vitrina.vitrinaVirtual.infraestructura.crud_interface;

import com.vitrina.vitrinaVirtual.infraestructura.entity.Notification;
import com.vitrina.vitrinaVirtual.infraestructura.entity.NotificationType;
import com.vitrina.vitrinaVirtual.infraestructura.entity.RecipientType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA para la entidad Notification
 */
public interface NotificationCrudRepository extends JpaRepository<Notification, Long> {

    /**
     * Obtener todas las notificaciones de un destinatario específico
     * Ordenadas por fecha de creación (más recientes primero)
     */
    List<Notification> findByRecipientIdAndRecipientTypeOrderByCreatedAtDesc(
            Long recipientId,
            RecipientType recipientType);

    /**
     * Obtener solo las notificaciones NO LEÍDAS de un destinatario
     */
    List<Notification> findByRecipientIdAndRecipientTypeAndIsReadFalseOrderByCreatedAtDesc(
            Long recipientId,
            RecipientType recipientType);

    /**
     * Contar notificaciones no leídas de un destinatario
     */
    long countByRecipientIdAndRecipientTypeAndIsReadFalse(
            Long recipientId,
            RecipientType recipientType);

    /**
     * Obtener notificaciones por tipo
     */
    List<Notification> findByRecipientIdAndRecipientTypeAndTypeOrderByCreatedAtDesc(
            Long recipientId,
            RecipientType recipientType,
            NotificationType type);

    /**
     * Obtener las N notificaciones más recientes de un destinatario
     */
    @Query("SELECT n FROM Notification n " +
            "WHERE n.recipientId = :recipientId " +
            "AND n.recipientType = :recipientType " +
            "ORDER BY n.createdAt DESC")
    List<Notification> findTopNByRecipient(
            @Param("recipientId") Long recipientId,
            @Param("recipientType") RecipientType recipientType);

    /**
     * Marcar todas las notificaciones de un destinatario como leídas
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt " +
            "WHERE n.recipientId = :recipientId " +
            "AND n.recipientType = :recipientType " +
            "AND n.isRead = false")
    int markAllAsReadByRecipient(
            @Param("recipientId") Long recipientId,
            @Param("recipientType") RecipientType recipientType,
            @Param("readAt") LocalDateTime readAt);

    /**
     * Eliminar notificaciones expiradas (para limpieza automática)
     */
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.expiresAt IS NOT NULL AND n.expiresAt < :now")
    int deleteExpiredNotifications(@Param("now") LocalDateTime now);

    /**
     * Obtener notificaciones relacionadas con una entidad específica
     */
    List<Notification> findByRelatedEntityIdAndRelatedEntityType(
            Long relatedEntityId,
            com.vitrina.vitrinaVirtual.infraestructura.entity.RelatedEntityType relatedEntityType);
}
