package com.vitrina.vitrinaVirtual.controller;

import com.vitrina.vitrinaVirtual.domain.dto.NotificationDto;
import com.vitrina.vitrinaVirtual.domain.service.notification.NotificationService;
import com.vitrina.vitrinaVirtual.infraestructura.crud_interface.ClienteCrudRepository;
import com.vitrina.vitrinaVirtual.infraestructura.crud_interface.UsuarioCrudRepository;
import com.vitrina.vitrinaVirtual.infraestructura.crud_interface.AlmacenCrudRepository;
import com.vitrina.vitrinaVirtual.infraestructura.entity.Cliente;
import com.vitrina.vitrinaVirtual.infraestructura.entity.Usuario;
import com.vitrina.vitrinaVirtual.infraestructura.entity.Almacen;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notificaciones", description = "Sistema de notificaciones para clientes y vendedores")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;
    private final UsuarioCrudRepository usuarioRepository;
    private final ClienteCrudRepository clienteRepository;
    private final AlmacenCrudRepository almacenRepository;

    @GetMapping("/client/me")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENT', 'ROLE_ADMIN')")
    @Operation(summary = "Obtener todas las notificaciones del cliente autenticado")
    public ResponseEntity<List<NotificationDto>> getAllForCurrentClient(Authentication authentication) {
        try {
            Long clientId = getClientIdFromAuth(authentication);
            List<NotificationDto> notifications = notificationService.getByClient(clientId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Error al obtener notificaciones: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/client/me/unread")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENT', 'ROLE_ADMIN')")
    @Operation(summary = "Obtener notificaciones NO LEÍDAS del cliente")
    public ResponseEntity<List<NotificationDto>> getUnreadForCurrentClient(Authentication authentication) {
        try {
            Long clientId = getClientIdFromAuth(authentication);
            List<NotificationDto> unreadNotifications = notificationService.getUnreadByClient(clientId);
            return ResponseEntity.ok(unreadNotifications);
        } catch (Exception e) {
            log.error("Error al obtener notificaciones no leídas: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/client/me/unread/count")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENT', 'ROLE_ADMIN')")
    @Operation(summary = "Contar notificaciones no leídas del cliente")
    public ResponseEntity<Map<String, Object>> getUnreadCountForCurrentClient(Authentication authentication) {
        try {
            Long clientId = getClientIdFromAuth(authentication);
            long count = notificationService.countUnreadByClient(clientId);

            Map<String, Object> response = new HashMap<>();
            response.put("count", count);
            response.put("hasUnread", count > 0);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al contar notificaciones: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/client/me/mark-all-read")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENT', 'ROLE_ADMIN')")
    @Operation(summary = "Marcar todas las notificaciones del cliente como leídas")
    public ResponseEntity<Map<String, Object>> markAllAsReadForCurrentClient(Authentication authentication) {
        try {
            Long clientId = getClientIdFromAuth(authentication);
            int updated = notificationService.markAllAsReadForClient(clientId);

            Map<String, Object> response = new HashMap<>();
            response.put("updated", updated);
            response.put("message", updated + " notificaciones marcadas como leídas");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al marcar notificaciones como leídas: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/store/me")
    @PreAuthorize("hasAnyAuthority('ROLE_VENDOR', 'ROLE_ADMIN')")
    @Operation(summary = "Obtener todas las notificaciones de la tienda autenticada")
    public ResponseEntity<List<NotificationDto>> getAllForCurrentStore(Authentication authentication) {
        try {
            Long storeId = getStoreIdFromAuth(authentication);
            List<NotificationDto> notifications = notificationService.getByStore(storeId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Error al obtener notificaciones de la tienda: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/store/me/unread")
    @PreAuthorize("hasAnyAuthority('ROLE_VENDOR', 'ROLE_ADMIN')")
    @Operation(summary = "Obtener notificaciones NO LEÍDAS de la tienda")
    public ResponseEntity<List<NotificationDto>> getUnreadForCurrentStore(Authentication authentication) {
        try {
            Long storeId = getStoreIdFromAuth(authentication);
            List<NotificationDto> unreadNotifications = notificationService.getUnreadByStore(storeId);
            return ResponseEntity.ok(unreadNotifications);
        } catch (Exception e) {
            log.error("Error al obtener notificaciones no leídas de tienda: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/store/me/unread/count")
    @PreAuthorize("hasAnyAuthority('ROLE_VENDOR', 'ROLE_ADMIN')")
    @Operation(summary = "Contar notificaciones no leídas de la tienda")
    public ResponseEntity<Map<String, Object>> getUnreadCountForCurrentStore(Authentication authentication) {
        try {
            Long storeId = getStoreIdFromAuth(authentication);
            long count = notificationService.countUnreadByStore(storeId);

            Map<String, Object> response = new HashMap<>();
            response.put("count", count);
            response.put("hasUnread", count > 0);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al contar notificaciones de tienda: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/store/me/mark-all-read")
    @PreAuthorize("hasAnyAuthority('ROLE_VENDOR', 'ROLE_ADMIN')")
    @Operation(summary = "Marcar todas las notificaciones de la tienda como leídas")
    public ResponseEntity<Map<String, Object>> markAllAsReadForCurrentStore(Authentication authentication) {
        try {
            Long storeId = getStoreIdFromAuth(authentication);
            int updated = notificationService.markAllAsReadForStore(storeId);

            Map<String, Object> response = new HashMap<>();
            response.put("updated", updated);
            response.put("message", updated + " notificaciones marcadas como leídas");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al marcar notificaciones de tienda como leídas: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENT', 'ROLE_VENDOR', 'ROLE_ADMIN')")
    @Operation(summary = "Marcar una notificación específica como leída")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificación marcada como leída"),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    public ResponseEntity<Map<String, Object>> markAsRead(
            @Parameter(description = "ID de la notificación") @PathVariable Long id,
            Authentication authentication) {
        try {
            notificationService.markAsRead(id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Notificación marcada como leída");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al marcar notificación {} como leída: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENT', 'ROLE_VENDOR', 'ROLE_ADMIN')")
    @Operation(summary = "Eliminar una notificación")
    public ResponseEntity<Void> deleteNotification(
            @Parameter(description = "ID de la notificación") @PathVariable Long id,
            Authentication authentication) {
        try {
            notificationService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error al eliminar notificación {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private Long getClientIdFromAuth(Authentication authentication) {
        String email = authentication.getName();

        Usuario usuario = usuarioRepository.findByCorreo(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Cliente cliente = clienteRepository.findByUsuario_IdUsuario(usuario.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Este usuario no tiene un perfil de cliente"));

        return cliente.getId();
    }

    private Long getStoreIdFromAuth(Authentication authentication) {
        String email = authentication.getName();

        Usuario usuario = usuarioRepository.findByCorreo(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Almacen almacen = almacenRepository.findByPropietario_IdUsuario(usuario.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Este usuario no tiene una tienda asociada"));

        return almacen.getId();
    }
}
