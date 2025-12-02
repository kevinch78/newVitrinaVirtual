package com.vitrina.vitrinaVirtual.domain.repository;

import java.util.List;
import java.util.Optional;

import com.vitrina.vitrinaVirtual.domain.dto.SavedOutfitDto;

public interface SavedOutfitRepository {
    
    SavedOutfitDto save(SavedOutfitDto dto, Long userId);
    
    List<SavedOutfitDto> findByUserId(Long userId);
    
    Optional<SavedOutfitDto> findByIdAndUserId(Long outfitId, Long userId);
    
    void deleteByIdAndUserId(Long outfitId, Long userId);
}