package com.vitrina.vitrinaVirtual.domain.repository;


import com.vitrina.vitrinaVirtual.infraestructura.entity.OutfitProduct;
import java.util.List;
import java.util.Optional;

public interface OutfitProductRepository {
    
    OutfitProduct save(OutfitProduct outfitProduct);
    
    Optional<OutfitProduct> findById(Long id);
    
    List<OutfitProduct> findByOutfitId(Long outfitId);
    
    void deleteById(Long id);
    
    void deleteByOutfitId(Long outfitId);
}