package com.vitrina.vitrinaVirtual.infraestructura.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "outfit_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutfitProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el outfit padre
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outfit_id", nullable = false)
    private SavedOutfit outfit;

    // Relación con producto original (puede ser null si el producto se elimina)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Producto producto;

    // Relación con tienda
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Almacen store;

    // ✅ SNAPSHOTS - Guardan el estado del producto al momento de guardarse
    @Column(length = 255)
    private String productName;

    @Column(precision = 10, scale = 2)
    private BigDecimal productPrice;

    @Column(length = 500)
    private String productImageUrl;

    @Column(length = 50)
    private String garmentType; // TOP, BOTTOM, OUTERWEAR, FOOTWEAR, ACCESSORIES

    @Column(nullable = false, updatable = false)
    private LocalDateTime addedAt;

    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
    }
}