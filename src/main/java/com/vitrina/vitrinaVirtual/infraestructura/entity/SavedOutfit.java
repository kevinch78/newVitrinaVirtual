package com.vitrina.vitrinaVirtual.infraestructura.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "saved_outfits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedOutfit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long outfitId;

    // ✅ CAMBIO: Relación con Cliente (no solo Long)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Cliente cliente;

    @Column(length = 255)
    private String outfitName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String accessory;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 50)
    private String gender;

    @Column(length = 50)
    private String climate;

    @Column(length = 50)
    private String style;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ✅ Relación bidireccional con productos del outfit
    @OneToMany(mappedBy = "outfit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OutfitProduct> products = new ArrayList<>();

    // ✅ Callbacks para fechas automáticas
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ✅ Método helper para agregar productos
    public void addProduct(OutfitProduct product) {
        products.add(product);
        product.setOutfit(this);
    }

    public void removeProduct(OutfitProduct product) {
        products.remove(product);
        product.setOutfit(null);
    }
}