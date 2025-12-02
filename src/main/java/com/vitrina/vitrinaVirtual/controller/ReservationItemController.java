package com.vitrina.vitrinaVirtual.controller;

import com.vitrina.vitrinaVirtual.domain.dto.ReservationItemDto;
import com.vitrina.vitrinaVirtual.domain.service.reservation.ReservationItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservation-items")
@Tag(name = "Items de Reserva", description = "Gestión de los productos individuales dentro de una reserva")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ReservationItemController {

    private final ReservationItemService service;
    private static final Log logger = LogFactory.getLog(ReservationItemController.class);

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CLIENT')")
    @Operation(summary = "Añadir un item a una reserva", description = "Crea un nuevo item de reserva (un producto asociado a una reserva).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item de reserva creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<ReservationItemDto> create(@RequestBody ReservationItemDto dto) {
        logger.info("Creando nuevo item de reserva: " + dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CLIENT', 'ROLE_VENDOR')")
    @Operation(summary = "Obtener un item de reserva por ID", description = "Obtiene los detalles de un item de reserva específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item de reserva encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Item no encontrado")
    })
    public ResponseEntity<ReservationItemDto> getById(@Parameter(description = "ID del item de reserva") @PathVariable Long id) {
        logger.info("Buscando item de reserva con ID: " + id);
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/reservation/{reservationId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CLIENT', 'ROLE_VENDOR')")
    @Operation(summary = "Obtener items por ID de reserva", description = "Obtiene todos los items asociados a una reserva específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Items encontrados"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<List<ReservationItemDto>> getByReservation(@Parameter(description = "ID de la reserva") @PathVariable Long reservationId) {
        logger.info("Buscando items para la reserva ID: " + reservationId);
        return ResponseEntity.ok(service.getByReservation(reservationId));
    }

    @GetMapping("/store/{storeId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VENDOR')")
    @Operation(summary = "Obtener items de reserva por tienda", description = "Obtiene todos los items de reserva pertenecientes a productos de una tienda específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Items encontrados"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<List<ReservationItemDto>> getByStore(@Parameter(description = "ID de la tienda") @PathVariable Long storeId) {
        logger.info("Buscando items de reserva para la tienda ID: " + storeId);
        return ResponseEntity.ok(service.getByStore(storeId));
    }

    @GetMapping("/store/{storeId}/status/{status}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VENDOR')")
    @Operation(summary = "Obtener items por tienda y estado", description = "Obtiene items de una tienda filtrando por el estado de la reserva (ej. 'PENDIENTE', 'CONFIRMADA').")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Items encontrados"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<List<ReservationItemDto>> getByStoreAndStatus(
            @Parameter(description = "ID de la tienda") @PathVariable Long storeId,
            @Parameter(description = "Estado de la reserva") @PathVariable String status) {
        logger.info("Buscando items para tienda ID: " + storeId + " con estado: " + status);
        return ResponseEntity.ok(service.getByStoreAndStatus(storeId, status));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VENDOR')")
    @Operation(summary = "Actualizar un item de reserva", description = "Actualiza el estado de un item de reserva (ej. para confirmar que el producto está listo).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Item no encontrado")
    })
    public ResponseEntity<ReservationItemDto> update(@Parameter(description = "ID del item a actualizar") @PathVariable Long id, @RequestBody ReservationItemDto dto) {
        logger.info("Actualizando item de reserva ID: " + id);
        dto.setId(id);
        return ResponseEntity.ok(service.update(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CLIENT')")
    @Operation(summary = "Eliminar un item de reserva", description = "Elimina un item de una reserva (ej. si el cliente ya no quiere ese producto).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item eliminado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Item no encontrado")
    })
    public ResponseEntity<Void> delete(@Parameter(description = "ID del item de reserva") @PathVariable Long id) {
        logger.info("Eliminando item de reserva ID: " + id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
