package com.vitrina.vitrinaVirtual.infraestructura.repositories;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.vitrina.vitrinaVirtual.domain.dto.SavedOutfitDto;
import com.vitrina.vitrinaVirtual.domain.repository.SavedOutfitRepository;
import com.vitrina.vitrinaVirtual.infraestructura.crud_interface.SavedOutfitCrudRepository;
import com.vitrina.vitrinaVirtual.infraestructura.entity.SavedOutfit;
import com.vitrina.vitrinaVirtual.infraestructura.mapper.SavedOutfitMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SavedOutfitRepositoryImpl implements SavedOutfitRepository {

    // ✅ CORRECCIÓN: Inyectar CrudRepository, no SavedOutfitRepository
    private final SavedOutfitCrudRepository crudRepository;
    private final SavedOutfitMapper mapper;

    @Override
    public SavedOutfitDto save(SavedOutfitDto dto, Long userId) {
        SavedOutfit entity = mapper.toEntity(dto);
        // ✅ Aquí debes setear el Cliente, no solo el userId
        // Lo haremos en el Service
        SavedOutfit saved = crudRepository.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public List<SavedOutfitDto> findByUserId(Long userId) {
        return crudRepository.findByCliente_Id(userId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SavedOutfitDto> findByIdAndUserId(Long outfitId, Long userId) {
        return crudRepository.findByOutfitIdAndCliente_Id(outfitId, userId)
                .map(mapper::toDto);
    }

    @Override
    public void deleteByIdAndUserId(Long outfitId, Long userId) {
        crudRepository.findByOutfitIdAndCliente_Id(outfitId, userId)
                .ifPresent(crudRepository::delete);
    }
}