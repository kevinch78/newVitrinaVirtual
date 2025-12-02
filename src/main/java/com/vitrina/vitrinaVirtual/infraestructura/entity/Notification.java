package com.vitrina.vitrinaVirtual.infraestructura.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad genérica de notificaciones del sistema.
 * Sirve tanto para clientes como para vendedores (stores).
 * Arquitectura escalable y profesional.
 */
@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_recipient", columnList = "recipient_id,recipient_type"),
        @Index(name = "idx_created_at", columnList = "created_at"),
        @Index(name = "idx_is_read", columnList = "is_read")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== DESTINATARIO ==========
    /**
     * ID del destinatario (puede ser clientId o storeId)
     */
    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

    /**
     * Tipo de destinatario (CLIENT o STORE)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "recipient_type", nullable = false, length = 20)
    private RecipientType recipientType;

    // ========== CONTENIDO ==========
    /**
     * Título de la notificación (breve)
     */
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    /**
     * Mensaje completo de la notificación
     */
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    /**
     * Tipo de notificación (para categorización y filtrado)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private NotificationType type;

    // ========== ENTIDAD RELACIONADA ==========
    /**
     * ID de la entidad relacionada (reserva, producto, etc.)
     * Puede ser null si no hay entidad relacionada
     */
    @Column(name = "related_entity_id")
    private Long relatedEntityId;

    /**
     * Tipo de entidad relacionada
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "related_entity_type", length = 30)
    @Builder.Default
    private RelatedEntityType relatedEntityType = RelatedEntityType.NONE;

    /**
     * URL de acción opcional (redirigir al hacer clic)
     */
    @Column(name = "action_url", length = 500)
    private String actionUrl;

    // ========== ESTADO ==========
    /**
     * Si la notificación fue leída o no
     */
    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    /**
     * Fecha y hora de creación
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Fecha y hora en que fue leída (null si no ha sido leída)
     */
    @Column(name = "read_at")
    private LocalDateTime readAt;

    // ========== METADATOS ADICIONALES ==========
    /**
     * Prioridad de la notificación (0=baja, 5=normal, 10=alta)
     */
    @Column(name = "priority")
    @Builder.Default
    private Integer priority = 5;

    /**
     * Si expira después de cierto tiempo (para limpieza automática)
     */
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    // ========== CALLBACKS ==========
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.isRead == null) {
            this.isRead = false;
        }
        if (this.priority == null) {
            this.priority = 5;
        }
        if (this.relatedEntityType == null) {
            this.relatedEntityType = RelatedEntityType.NONE;
        }
    }

    /**
     * Marca la notificación como leída
     */
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    /**
     * Verifica si la notificación ha expirado
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
}
