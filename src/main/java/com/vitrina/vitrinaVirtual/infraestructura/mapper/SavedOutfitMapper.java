package com.vitrina.vitrinaVirtual.infraestructura.mapper;

import com.vitrina.vitrinaVirtual.domain.dto.OutfitProductDto;
import com.vitrina.vitrinaVirtual.domain.dto.SavedOutfitDto;
import com.vitrina.vitrinaVirtual.infraestructura.entity.SavedOutfit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SavedOutfitMapper {

    private final OutfitProductMapper outfitProductMapper;

    // ==================== Entity → DTO ====================
    public SavedOutfitDto toDto(SavedOutfit entity) {
        if (entity == null) {
            return null;
        }

        SavedOutfitDto dto = new SavedOutfitDto();
        dto.setOutfitId(entity.getOutfitId());
        dto.setUserId(entity.getCliente() != null ? entity.getCliente().getId() : null);
        dto.setOutfitName(entity.getOutfitName());
        dto.setDescription(entity.getDescription());
        dto.setAccessory(entity.getAccessory());
        dto.setImageUrl(entity.getImageUrl());
        dto.setGender(entity.getGender());
        dto.setClimate(entity.getClimate());
        dto.setStyle(entity.getStyle());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Mapear productos usando el mapper especializado
        if (entity.getProducts() != null && !entity.getProducts().isEmpty()) {
            List<OutfitProductDto> productDtos = entity.getProducts().stream()
                    .map(outfitProductMapper::toDto)
                    .collect(Collectors.toList());
            dto.setProducts(productDtos);

            // Calcular precio total
            BigDecimal total = productDtos.stream()
                    .map(OutfitProductDto::getProductPrice)
                    .filter(price -> price != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setTotalPrice(total);
        }

        return dto;
    }

    // ==================== DTO → Entity ====================
    public SavedOutfit toEntity(SavedOutfitDto dto) {
        if (dto == null) {
            return null;
        }

        SavedOutfit entity = SavedOutfit.builder()
                .outfitId(dto.getOutfitId())
                .outfitName(dto.getOutfitName())
                .description(dto.getDescription())
                .accessory(dto.getAccessory())
                .imageUrl(dto.getImageUrl())
                .gender(dto.getGender())
                .climate(dto.getClimate())
                .style(dto.getStyle())
                .build();

        // NO mapeamos productos aquí, se manejan por separado en el servicio

        return entity;
    }
}