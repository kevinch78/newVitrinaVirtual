package com.vitrina.vitrinaVirtual.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class SavedOutfitDto {

    private Long outfitId;
    private Long userId;
    private String outfitName;
    private String description;
    private String accessory;
    private String imageUrl;
    private String gender;
    private String climate;
    private String style;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<OutfitProductDto> products;

    // Campo transitorio para el total del outfit
    private BigDecimal totalPrice;

    // Getters y Setters
}
