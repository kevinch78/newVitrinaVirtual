package com.vitrina.vitrinaVirtual.infraestructura.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.vitrina.vitrinaVirtual.domain.repository.OutfitProductRepository;
import com.vitrina.vitrinaVirtual.infraestructura.crud_interface.OutfitProductCrudRepository;
import com.vitrina.vitrinaVirtual.infraestructura.entity.OutfitProduct;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OutfitProductRepositoryImpl implements OutfitProductRepository {

    private final OutfitProductCrudRepository crud;

    @Override
    public OutfitProduct save(OutfitProduct outfitProduct) {
        return crud.save(outfitProduct);
    }

    @Override
    public Optional<OutfitProduct> findById(Long id) {
        return crud.findById(id);
    }

    @Override
    public List<OutfitProduct> findByOutfitId(Long outfitId) {
        return crud.findByOutfit_OutfitId(outfitId);
    }

    @Override
    public void deleteById(Long id) {
        crud.deleteById(id);
    }

    @Override
    public void deleteByOutfitId(Long outfitId) {
        crud.deleteByOutfit_OutfitId(outfitId);
    }
}