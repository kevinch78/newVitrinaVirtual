package com.vitrina.vitrinaVirtual.infraestructura.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "reservations")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cliente que hizo la reserva
    @Column(nullable = false)
    private Long clientId;

    // Tienda a la que pertenece la reserva
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Almacen store;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // PENDING, PARTIALLY_ACCEPTED, READY, DELIVERED, CANCELLED
    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Double totalPrice;

    // Relación con ReservationItem
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationItem> items;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = "PENDING";  // Estado inicial
        }
        if (this.totalPrice == null) {
            this.totalPrice = 0.0;  // Precio inicial
        }
    }
}
