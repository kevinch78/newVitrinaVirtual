package com.vitrina.vitrinaVirtual.infraestructura.crud_interface;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vitrina.vitrinaVirtual.infraestructura.entity.SavedOutfit;

public interface SavedOutfitCrudRepository extends JpaRepository<SavedOutfit, Long> {
    
    List<SavedOutfit> findByCliente_Id(Long clienteId);
    
    Optional<SavedOutfit> findByOutfitIdAndCliente_Id(Long outfitId, Long clienteId);
}