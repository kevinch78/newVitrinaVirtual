package com.vitrina.vitrinaVirtual.controller;

import com.vitrina.vitrinaVirtual.domain.dto.ReservationDto;
import com.vitrina.vitrinaVirtual.domain.service.reservation.ReservationService;
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
@RequestMapping("/api/reservations")
@Tag(name = "Reservas", description = "Gestión de reservas de productos")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private static final Log logger = LogFactory.getLog(ReservationController.class);

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    @Operation(summary = "Crear una nueva reserva", description = "Permite a un cliente crear una nueva reserva.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de reserva inválidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<ReservationDto> create(@RequestBody ReservationDto dto) {
        logger.info("Petición para crear reserva: " + dto);
        ReservationDto created = reservationService.create(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CLIENT', 'ROLE_VENDOR')")
    @Operation(summary = "Obtener una reserva por ID", description = "Obtiene los detalles de una reserva específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva encontrada"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    public ResponseEntity<ReservationDto> getById(@Parameter(description = "ID de la reserva") @PathVariable Long id) {
        logger.info("Petición para obtener reserva con ID: " + id);
        // Nota: La capa de servicio debería verificar que el CLIENTE o VENDEDOR solo pueda ver reservas que le pertenecen.
        ReservationDto dto = reservationService.getById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CLIENT')")
    @Operation(summary = "Obtener reservas por cliente", description = "Obtiene una lista de todas las reservas de un cliente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservas encontradas"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<List<ReservationDto>> getByClient(@Parameter(description = "ID del cliente") @PathVariable Long clientId) {
        logger.info("Petición para obtener reservas del cliente con ID: " + clientId);
        // Nota: Se debe asegurar que un CLIENTE solo pueda consultar sus propias reservas.
        List<ReservationDto> list = reservationService.getByClient(clientId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/store/{storeId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VENDOR')")
    @Operation(summary = "Obtener reservas por tienda", description = "Obtiene una lista de todas las reservas asociadas a una tienda.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservas encontradas"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<List<ReservationDto>> getByStore(@Parameter(description = "ID de la tienda") @PathVariable Long storeId) {
        logger.info("Petición para obtener reservas de la tienda con ID: " + storeId);
        // Nota: La capa de servicio debería verificar que un VENDEDOR solo pueda ver las reservas de su tienda.
        List<ReservationDto> list = reservationService.getByStore(storeId);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CLIENT', 'ROLE_VENDOR')")
    @Operation(summary = "Actualizar una reserva", description = "Actualiza el estado o detalles de una reserva. Un cliente puede cancelar, un vendedor puede confirmar, etc.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva actualizada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    public ResponseEntity<ReservationDto> update(
            @Parameter(description = "ID de la reserva a actualizar") @PathVariable Long id,
            @RequestBody ReservationDto dto
    ) {
        logger.info("Petición para actualizar reserva con ID: " + id);
        dto.setId(id);
        ReservationDto updated = reservationService.update(dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CLIENT')")
    @Operation(summary = "Eliminar una reserva", description = "Elimina una reserva. Principalmente para que un cliente la cancele o un admin la elimine.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reserva eliminada"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    public ResponseEntity<Void> delete(@Parameter(description = "ID de la reserva") @PathVariable Long id) {
        logger.info("Petición para eliminar reserva con ID: " + id);
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
