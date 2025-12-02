package com.vitrina.vitrinaVirtual.infraestructura.entity;

/**
 * Tipos de notificaciones del sistema
 */
public enum NotificationType {
    // ========== Para CLIENTES ==========
    /**
     * Vendedor aceptó la reserva del cliente
     */
    RESERVATION_ACCEPTED,

    /**
     * Vendedor rechazó la reserva del cliente
     */
    RESERVATION_REJECTED,

    /**
     * Reserva está lista para recoger
     */
    RESERVATION_READY,

    /**
     * Recordatorio de reserva pendiente
     */
    RESERVATION_REMINDER,

    /**
     * Reserva fue entregada
     */
    RESERVATION_DELIVERED,

    // ========== Para VENDEDORES (STORES) ==========
    /**
     * Nueva reserva recibida
     */
    NEW_RESERVATION,

    /**
     * Cliente canceló una reserva
     */
    RESERVATION_CANCELLED,

    /**
     * Producto con stock bajo
     */
    LOW_STOCK,

    /**
     * Producto agotado
     */
    OUT_OF_STOCK,

    // ========== Generales ==========
    /**
     * Notificación general del sistema
     */
    SYSTEM,

    /**
     * Mensaje informativo
     */
    INFO
}
