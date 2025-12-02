package com.vitrina.vitrinaVirtual.infraestructura.mapper;

import com.vitrina.vitrinaVirtual.domain.dto.OutfitProductDto;
import com.vitrina.vitrinaVirtual.infraestructura.entity.OutfitProduct;
import org.springframework.stereotype.Component;

@Component
public class OutfitProductMapper {

    // ==================== Entity → DTO ====================
    public OutfitProductDto toDto(OutfitProduct entity) {
        if (entity == null) {
            return null;
        }

        OutfitProductDto dto = new OutfitProductDto();
        dto.setId(entity.getId());
        dto.setOutfitId(entity.getOutfit() != null ? entity.getOutfit().getOutfitId() : null);
        dto.setProductId(entity.getProducto() != null ? entity.getProducto().getIdProducto() : null);
        
        // ✅ CORRECCIÓN: Usar getId() en lugar de getAlmacenId()
        dto.setStoreId(entity.getStore() != null ? entity.getStore().getId() : null);
        
        dto.setProductName(entity.getProductName());
        dto.setProductPrice(entity.getProductPrice());
        dto.setProductImageUrl(entity.getProductImageUrl());
        dto.setGarmentType(entity.getGarmentType());
        
        // Nombre de la tienda
        if (entity.getStore() != null) {
            dto.setStoreName(entity.getStore().getNombre());
        }

        return dto;
    }

    // ==================== DTO → Entity ====================
    public OutfitProduct toEntity(OutfitProductDto dto) {
        if (dto == null) {
            return null;
        }

        return OutfitProduct.builder()
                .id(dto.getId())
                .productName(dto.getProductName())
                .productPrice(dto.getProductPrice())
                .productImageUrl(dto.getProductImageUrl())
                .garmentType(dto.getGarmentType())
                .build();
    }
}