package com.vitrina.vitrinaVirtual.domain.service.reservation;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vitrina.vitrinaVirtual.domain.dto.ReservationDto;
import com.vitrina.vitrinaVirtual.domain.repository.ReservationRepository;
import com.vitrina.vitrinaVirtual.domain.service.notification.NotificationService;
import com.vitrina.vitrinaVirtual.infraestructura.crud_interface.ClienteCrudRepository;
import com.vitrina.vitrinaVirtual.infraestructura.crud_interface.AlmacenCrudRepository;
import com.vitrina.vitrinaVirtual.infraestructura.entity.Cliente;
import com.vitrina.vitrinaVirtual.infraestructura.entity.Almacen;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de reservaciones con integración de notificaciones automáticas
 * Modo PRO 🚀
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final NotificationService notificationService;
    private final ClienteCrudRepository clienteRepository;
    private final AlmacenCrudRepository almacenRepository;

    @Override
    @Transactional
    public ReservationDto create(ReservationDto dto) {
        log.info("📝 Creando nueva reserva para cliente {} en tienda {}", dto.getClientId(), dto.getStoreId());

        // Guardar la reserva
        ReservationDto savedReservation = reservationRepository.save(dto);

        // 🔔 NOTIFICACIÓN: Avisar a la tienda sobre la nueva reserva
        try {
            Cliente cliente = clienteRepository.findById(dto.getClientId())
                    .orElse(null);

            String clientName = cliente != null && cliente.getUsuario() != null
                    ? cliente.getUsuario().getNombre()
                    : "Un cliente";

            notificationService.notifyStoreNewReservation(
                    dto.getStoreId(),
                    savedReservation.getId(),
                    clientName);

            log.info("✅ Notificación enviada a tienda {} sobre nueva reserva #{}",
                    dto.getStoreId(), savedReservation.getId());
        } catch (Exception e) {
            log.error("❌ Error al enviar notificación a tienda: {}", e.getMessage());
            // No fallar la reserva si falla la notificación
        }

        return savedReservation;
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationDto getById(Long id) {
        return reservationRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDto> getByClient(Long clientId) {
        return reservationRepository.findByClientId(clientId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDto> getByStore(Long storeId) {
        return reservationRepository.findByStoreId(storeId);
    }

    @Override
    @Transactional
    public ReservationDto update(ReservationDto dto) {
        log.info("📝 Actualizando reserva {} con estado: {}", dto.getId(), dto.getStatus());

        // Obtener el estado anterior para comparar
        ReservationDto oldReservation = reservationRepository.findById(dto.getId())
                .orElse(null);

        // Actualizar la reserva
        ReservationDto updatedReservation = reservationRepository.save(dto);

        // 🔔 NOTIFICACIONES según cambio de estado
        if (oldReservation != null && !oldReservation.getStatus().equals(dto.getStatus())) {
            try {
                handleStatusChangeNotifications(oldReservation, updatedReservation);
            } catch (Exception e) {
                log.error("❌ Error al enviar notificaciones por cambio de estado: {}", e.getMessage());
            }
        }

        return updatedReservation;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("🗑️ Eliminando/Cancelando reserva {}", id);

        // Obtener la reserva antes de eliminarla
        ReservationDto reservation = reservationRepository.findById(id).orElse(null);

        if (reservation != null) {
            // 🔔 NOTIFICACIÓN: Avisar a la tienda que se canceló la reserva
            try {
                Cliente cliente = clienteRepository.findById(reservation.getClientId())
                        .orElse(null);

                String clientName = cliente != null && cliente.getUsuario() != null
                        ? cliente.getUsuario().getNombre()
                        : "Un cliente";

                notificationService.notifyStoreReservationCancelled(
                        reservation.getStoreId(),
                        id,
                        clientName);

                log.info("✅ Notificación de cancelación enviada a tienda {}", reservation.getStoreId());
            } catch (Exception e) {
                log.error("❌ Error al enviar notificación de cancelación: {}", e.getMessage());
            }
        }

        reservationRepository.deleteById(id);
    }

    /**
     * Maneja las notificaciones según el cambio de estado de la reserva
     */
    private void handleStatusChangeNotifications(ReservationDto oldReservation, ReservationDto newReservation) {
        String newStatus = newReservation.getStatus();
        Long clientId = newReservation.getClientId();
        Long storeId = newReservation.getStoreId();
        Long reservationId = newReservation.getId();

        // Obtener nombre de la tienda
        Almacen almacen = almacenRepository.findById(storeId).orElse(null);
        String storeName = almacen != null ? almacen.getNombre() : "La tienda";

        log.info("📬 Procesando notificaciones por cambio de estado: {} -> {}",
                oldReservation.getStatus(), newStatus);

        switch (newStatus) {
            case "ACCEPTED":
            case "PARTIALLY_ACCEPTED":
                // Notificar al cliente que su reserva fue aceptada
                notificationService.notifyClientReservationAccepted(clientId, reservationId, storeName);
                log.info("✅ Cliente {} notificado: reserva aceptada", clientId);
                break;

            case "REJECTED":
            case "CANCELLED":
                // Notificar al cliente que su reserva fue rechazada/cancelada
                notificationService.notifyClientReservationRejected(
                        clientId,
                        reservationId,
                        storeName,
                        "La tienda no pudo procesar tu solicitud");
                log.info("✅ Cliente {} notificado: reserva rechazada/cancelada", clientId);
                break;

            case "READY":
                // Notificar al cliente que su reserva está lista para recoger
                notificationService.notifyClientReservationReady(clientId, reservationId, storeName);
                log.info("✅ Cliente {} notificado: reserva lista", clientId);
                break;

            case "DELIVERED":
                // Podríamos agregar una notificación de "Gracias por tu compra"
                log.info("📦 Reserva {} entregada al cliente {}", reservationId, clientId);
                break;

            default:
                log.debug("Estado {} no requiere notificación especial", newStatus);
        }
    }
}
