package com.vitrina.vitrinaVirtual.infraestructura.crud_interface;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vitrina.vitrinaVirtual.infraestructura.entity.OutfitProduct;

public interface OutfitProductCrudRepository extends JpaRepository<OutfitProduct, Long> {
    
    List<OutfitProduct> findByOutfit_OutfitId(Long outfitId);
    
    void deleteByOutfit_OutfitId(Long outfitId);
}