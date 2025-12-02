package com.vitrina.vitrinaVirtual.infraestructura.mapper;

import com.vitrina.vitrinaVirtual.domain.dto.NotificationDto;
import com.vitrina.vitrinaVirtual.infraestructura.entity.Notification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper manual para Notification (sin MapStruct para evitar problemas de
 * compilación)
 * Implementación simple y directa
 */
@Component
public class NotificationMapperManual {

    /**
     * Convierte una entidad Notification a DTO
     */
    public NotificationDto toDto(Notification entity) {
        if (entity == null) {
            return null;
        }

        return NotificationDto.builder()
                .id(entity.getId())
                .recipientId(entity.getRecipientId())
                .recipientType(entity.getRecipientType())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .type(entity.getType())
                .relatedEntityId(entity.getRelatedEntityId())
                .relatedEntityType(entity.getRelatedEntityType())
                .actionUrl(entity.getActionUrl())
                .isRead(entity.getIsRead())
                .createdAt(entity.getCreatedAt())
                .readAt(entity.getReadAt())
                .priority(entity.getPriority())
                .expiresAt(entity.getExpiresAt())
                .build();
    }

    /**
     * Convierte un DTO a entidad Notification
     */
    public Notification toEntity(NotificationDto dto) {
        if (dto == null) {
            return null;
        }

        return Notification.builder()
                .id(dto.getId())
                .recipientId(dto.getRecipientId())
                .recipientType(dto.getRecipientType())
                .title(dto.getTitle())
                .message(dto.getMessage())
                .type(dto.getType())
                .relatedEntityId(dto.getRelatedEntityId())
                .relatedEntityType(dto.getRelatedEntityType())
                .actionUrl(dto.getActionUrl())
                .isRead(dto.getIsRead())
                .createdAt(dto.getCreatedAt())
                .readAt(dto.getReadAt())
                .priority(dto.getPriority())
                .expiresAt(dto.getExpiresAt())
                .build();
    }

    /**
     * Convierte una lista de entidades a DTOs
     */
    public List<NotificationDto> toDtoList(List<Notification> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una lista de DTOs a entidades
     */
    public List<Notification> toEntityList(List<NotificationDto> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
