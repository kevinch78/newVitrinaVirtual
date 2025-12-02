package com.vitrina.vitrinaVirtual.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationItemDto {

    private Long id;
    private Long reservationId;  // ✅ Agregar esto
    
    private Long productId;
    private Long storeId;
    private ProductDto product; // 👈 CAMBIO CLAVE: Objeto completo en vez de campos sueltos
    private int quantity;
    private Double priceSnapshot;

    private String status;

    // Agrega esto manualmente al final de la clase ReservationItemDto
    public Long getReservationId() {
        return this.reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }
}