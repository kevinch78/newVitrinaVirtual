package com.vitrina.vitrinaVirtual.domain.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vitrina.vitrinaVirtual.domain.dto.OutfitProductDto;
import com.vitrina.vitrinaVirtual.domain.dto.SavedOutfitDto;
import com.vitrina.vitrinaVirtual.infraestructura.crud_interface.ClienteCrudRepository;
import com.vitrina.vitrinaVirtual.infraestructura.crud_interface.ProductoCrudRepository;
import com.vitrina.vitrinaVirtual.infraestructura.crud_interface.SavedOutfitCrudRepository;
import com.vitrina.vitrinaVirtual.infraestructura.entity.Cliente;
import com.vitrina.vitrinaVirtual.infraestructura.entity.OutfitProduct;
import com.vitrina.vitrinaVirtual.infraestructura.entity.SavedOutfit;
import com.vitrina.vitrinaVirtual.infraestructura.mapper.SavedOutfitMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SavedOutfitServiceImpl implements SavedOutfitService {

    private final SavedOutfitCrudRepository outfitCrudRepository;
    private final ClienteCrudRepository clienteCrudRepository;
    private final ProductoCrudRepository productoCrudRepository;
    private final SavedOutfitMapper mapper;

    @Override
    @Transactional
    public SavedOutfitDto saveOutfit(SavedOutfitDto outfitDto, Long userId) {
        log.info("💾 Guardando outfit para usuario: {}", userId);
        
        // 1. Buscar el cliente
        Cliente cliente = clienteCrudRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + userId));
        
        // 2. Convertir DTO a Entity
        SavedOutfit outfit = mapper.toEntity(outfitDto);
        outfit.setCliente(cliente);
        
        // 3. Procesar productos del outfit
        if (outfitDto.getProducts() != null && !outfitDto.getProducts().isEmpty()) {
            List<OutfitProduct> outfitProducts = new ArrayList<>();
            
            for (OutfitProductDto productDto : outfitDto.getProducts()) {
                OutfitProduct outfitProduct = OutfitProduct.builder()
                        .productName(productDto.getProductName())
                        .productPrice(productDto.getProductPrice())
                        .productImageUrl(productDto.getProductImageUrl())
                        .garmentType(productDto.getGarmentType())
                        .build();
                
                // ✅ Vincular con el producto original si existe
                if (productDto.getProductId() != null) {
                    productoCrudRepository.findById(productDto.getProductId())
                            .ifPresent(producto -> {
                                outfitProduct.setProducto(producto);
                                outfitProduct.setStore(producto.getAlmacen());
                            });
                }
                
                // ✅ Vincular con el outfit
                outfit.addProduct(outfitProduct);
                outfitProducts.add(outfitProduct);
            }
        }
        
        // 4. Guardar outfit (cascade guarda los productos automáticamente)
        SavedOutfit savedOutfit = outfitCrudRepository.save(outfit);
        
        log.info("✅ Outfit guardado con ID: {}", savedOutfit.getOutfitId());
        
        // 5. Convertir a DTO y retornar
        return mapper.toDto(savedOutfit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SavedOutfitDto> getUserOutfits(Long userId) {
        log.info("📦 Obteniendo outfits del usuario: {}", userId);
        
        List<SavedOutfit> outfits = outfitCrudRepository.findByCliente_Id(userId);
        
        log.info("✅ Encontrados {} outfits", outfits.size());
        
        return outfits.stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SavedOutfitDto getOutfitById(Long outfitId, Long userId) {
        log.info("🔍 Buscando outfit {} del usuario {}", outfitId, userId);
        
        return outfitCrudRepository.findByOutfitIdAndCliente_Id(outfitId, userId)
                .map(mapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Outfit no encontrado o no pertenece al usuario"));
    }

    @Override
    @Transactional
    public void deleteOutfit(Long outfitId, Long userId) {
        log.info("🗑️ Eliminando outfit {} del usuario {}", outfitId, userId);
        
        SavedOutfit outfit = outfitCrudRepository.findByOutfitIdAndCliente_Id(outfitId, userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Outfit no encontrado o no pertenece al usuario"));
        
        outfitCrudRepository.delete(outfit);
        
        log.info("✅ Outfit eliminado correctamente");
    }

    @Override
    @Transactional
    public SavedOutfitDto updateOutfit(Long outfitId, SavedOutfitDto outfitDto, Long userId) {
        log.info("📝 Actualizando outfit {} del usuario {}", outfitId, userId);
        
        // Buscar outfit existente
        SavedOutfit existingOutfit = outfitCrudRepository.findByOutfitIdAndCliente_Id(outfitId, userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Outfit no encontrado o no pertenece al usuario"));
        
        // Actualizar campos básicos
        existingOutfit.setOutfitName(outfitDto.getOutfitName());
        existingOutfit.setDescription(outfitDto.getDescription());
        existingOutfit.setAccessory(outfitDto.getAccessory());
        existingOutfit.setImageUrl(outfitDto.getImageUrl());
        existingOutfit.setGender(outfitDto.getGender());
        existingOutfit.setClimate(outfitDto.getClimate());
        existingOutfit.setStyle(outfitDto.getStyle());
        
        // Guardar cambios
        SavedOutfit updatedOutfit = outfitCrudRepository.save(existingOutfit);
        
        log.info("✅ Outfit actualizado correctamente");
        
        return mapper.toDto(updatedOutfit);
    }
}