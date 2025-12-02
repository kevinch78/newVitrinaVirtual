package com.vitrina.vitrinaVirtual.infraestructura.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reservation_items")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ReservationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Reservation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    // Producto original
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Producto producto;

    // Tienda propietaria del producto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Almacen store;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private Double priceSnapshot;

    // Estado por tienda: PENDING, ACCEPTED, REJECTED, READY, DELIVERED
    @Column(nullable = false)
    private String status;

    // ✅ Método que inicializa campos antes de guardar
    @PrePersist
    protected void onCreate() {
        if (this.priceSnapshot == null && this.producto != null) {
            this.priceSnapshot = this.producto.getPrecio();  // Captura precio actual
        }
        if (this.status == null) {
            this.status = "PENDING";  // Estado inicial
        }
        if (this.quantity == 0) {
            this.quantity = 1;  // Cantidad mínima
        }
    }
}