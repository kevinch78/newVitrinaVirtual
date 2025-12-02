package com.vitrina.vitrinaVirtual.domain.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OutfitProductDto {

    private Long id;
    private Long outfitId;
    private Long productId;
    private Long storeId;
    private String productName;
    private BigDecimal productPrice;
    private String productImageUrl;
    private String garmentType;
    private String storeName; // Opcional, join con almacen

    // Getters y Setters
}
