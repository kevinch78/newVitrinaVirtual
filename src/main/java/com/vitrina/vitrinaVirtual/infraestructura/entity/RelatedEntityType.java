package com.vitrina.vitrinaVirtual.infraestructura.entity;

/**
 * Tipo de entidad relacionada con la notificación
 */
public enum RelatedEntityType {
    /**
     * Relacionada con una reserva completa
     */
    RESERVATION,

    /**
     * Relacionada con un item específico de reserva
     */
    RESERVATION_ITEM,

    /**
     * Relacionada con un producto
     */
    PRODUCT,

    /**
     * Relacionada con una tienda
     */
    STORE,

    /**
     * Relacionada con un outfit guardado
     */
    SAVED_OUTFIT,

    /**
     * Sin entidad relacionada
     */
    NONE
}
