package com.vitrina.vitrinaVirtual.controller;

import com.vitrina.vitrinaVirtual.domain.dto.StoreNotificationDto;
import com.vitrina.vitrinaVirtual.domain.service.reservation.StoreNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notificaciones de Tienda", description = "Gestión de notificaciones para vendedores")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class StoreNotificationController {

    private final StoreNotificationService notificationService;
    private static final Log logger = LogFactory.getLog(StoreNotificationController.class);

    @GetMapping("/store/{storeId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VENDOR')")
    @Operation(summary = "Obtener notificaciones por tienda", description = "Obtiene todas las notificaciones para una tienda específica.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notificaciones encontradas"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "404", description = "Tienda no encontrada")
    })
    public ResponseEntity<List<StoreNotificationDto>> getByStore(@Parameter(description = "ID de la tienda") @PathVariable Long storeId) {
        logger.info("Solicitando notificaciones para la tienda ID: " + storeId);
        // Nota: Se debería añadir una comprobación para asegurar que el ROL 'VENDOR' solo accede a su propia tienda.
        List<StoreNotificationDto> notifications = notificationService.getByStore(storeId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/store/{storeId}/unread")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VENDOR')")
    @Operation(summary = "Obtener notificaciones no leídas", description = "Obtiene todas las notificaciones no leídas para una tienda específica.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notificaciones encontradas"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "404", description = "Tienda no encontrada")
    })
    public ResponseEntity<List<StoreNotificationDto>> getUnreadByStore(@Parameter(description = "ID de la tienda") @PathVariable Long storeId) {
        logger.info("Solicitando notificaciones no leídas para la tienda ID: " + storeId);
        List<StoreNotificationDto> notifications = notificationService.getUnreadByStore(storeId);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{notificationId}/read")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VENDOR')")
    @Operation(summary = "Marcar notificación como leída", description = "Marca una notificación específica como leída.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Notificación marcada como leída"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    public ResponseEntity<Void> markAsRead(@Parameter(description = "ID de la notificación") @PathVariable Long notificationId) {
        logger.info("Marcando como leída la notificación ID: " + notificationId);
        notificationService.markAsRead(notificationId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VENDOR')")
    @Operation(summary = "Eliminar notificación", description = "Elimina una notificación específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Notificación eliminada exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    public ResponseEntity<Void> deleteNotification(@Parameter(description = "ID de la notificación") @PathVariable Long notificationId) {
        logger.info("Eliminando la notificación ID: " + notificationId);
        notificationService.delete(notificationId);
        return ResponseEntity.noContent().build();
    }
}