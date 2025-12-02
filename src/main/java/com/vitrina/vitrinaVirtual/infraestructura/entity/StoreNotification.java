package com.vitrina.vitrinaVirtual.infraestructura.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "store_notifications")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class StoreNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Almacen store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_item_id", nullable = false)
    private ReservationItem reservationItem;

    private String message;

    private boolean isRead;

    private LocalDateTime createdAt;
}