package com.vitrina.vitrinaVirtual.domain.dto;

import com.vitrina.vitrinaVirtual.infraestructura.entity.NotificationType;
import com.vitrina.vitrinaVirtual.infraestructura.entity.RecipientType;
import com.vitrina.vitrinaVirtual.infraestructura.entity.RelatedEntityType;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos de notificaciones
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {

    private Long id;

    // Destinatario
    private Long recipientId;
    private RecipientType recipientType;

    // Contenido
    private String title;
    private String message;
    private NotificationType type;

    // Entidad relacionada
    private Long relatedEntityId;
    private RelatedEntityType relatedEntityType;
    private String actionUrl;

    // Estado
    private Boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    // Metadatos
    private Integer priority;
    private LocalDateTime expiresAt;

    /**
     * Constructor simplificado para crear notificaciones rápidamente
     */
    public NotificationDto(Long recipientId, RecipientType recipientType,
            String title, String message, NotificationType type) {
        this.recipientId = recipientId;
        this.recipientType = recipientType;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = false;
        this.priority = 5;
        this.relatedEntityType = RelatedEntityType.NONE;
    }
}
