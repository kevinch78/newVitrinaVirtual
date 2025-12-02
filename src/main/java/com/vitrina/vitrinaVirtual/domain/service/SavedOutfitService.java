package com.vitrina.vitrinaVirtual.domain.service;


import java.util.List;

import com.vitrina.vitrinaVirtual.domain.dto.SavedOutfitDto;

public interface SavedOutfitService {
    
    /**
     * Guarda un nuevo outfit para un usuario
     */
    SavedOutfitDto saveOutfit(SavedOutfitDto outfitDto, Long userId);
    
    /**
     * Obtiene todos los outfits guardados de un usuario
     */
    List<SavedOutfitDto> getUserOutfits(Long userId);
    
    /**
     * Obtiene un outfit específico por ID (solo si pertenece al usuario)
     */
    SavedOutfitDto getOutfitById(Long outfitId, Long userId);
    
    /**
     * Elimina un outfit (solo si pertenece al usuario)
     */
    void deleteOutfit(Long outfitId, Long userId);
    
    /**
     * Actualiza un outfit existente
     */
    SavedOutfitDto updateOutfit(Long outfitId, SavedOutfitDto outfitDto, Long userId);
}
