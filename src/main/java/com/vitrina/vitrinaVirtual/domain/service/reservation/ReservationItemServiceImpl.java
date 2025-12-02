package com.vitrina.vitrinaVirtual.domain.service.reservation;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.vitrina.vitrinaVirtual.domain.dto.ReservationItemDto;
import com.vitrina.vitrinaVirtual.domain.repository.ReservationItemRepository;
import com.vitrina.vitrinaVirtual.infraestructura.crud_interface.ProductoCrudRepository;
import com.vitrina.vitrinaVirtual.infraestructura.crud_interface.AlmacenCrudRepository;
import com.vitrina.vitrinaVirtual.infraestructura.crud_interface.ReservationCrudRepository;
import com.vitrina.vitrinaVirtual.infraestructura.crud_interface.ReservationItemCrudRepository; // ✅ Nuevo import
import com.vitrina.vitrinaVirtual.infraestructura.entity.Producto;
import com.vitrina.vitrinaVirtual.infraestructura.entity.Almacen;
import com.vitrina.vitrinaVirtual.infraestructura.entity.Reservation;
import com.vitrina.vitrinaVirtual.infraestructura.entity.ReservationItem;
import com.vitrina.vitrinaVirtual.infraestructura.mapper.ReservationItemMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationItemServiceImpl implements ReservationItemService {

    private final ReservationItemRepository repository;
    private final ProductoCrudRepository productoRepository;
    private final AlmacenCrudRepository almacenRepository;
    private final ReservationCrudRepository reservationRepository;
    private final ReservationItemCrudRepository reservationItemCrudRepository; // ✅ Nueva inyección
    private final ReservationItemMapper mapper;

    @Override
    public ReservationItemDto create(ReservationItemDto dto) {
        // 1. Buscar el producto
        Producto producto = productoRepository.findById(dto.getProductId())
            .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + dto.getProductId()));
        
        // 2. Buscar la tienda
        Almacen store = almacenRepository.findById(dto.getStoreId())
            .orElseThrow(() -> new RuntimeException("Tienda no encontrada: " + dto.getStoreId()));

        // 3. Buscar la reserva (✅ NUEVO: Necesario para vincular el item)
        Reservation reservation = reservationRepository.findById(dto.getReservationId())
             .orElseThrow(() -> new RuntimeException("Reserva no encontrada: " + dto.getReservationId()));
        
        // 4. Convertir DTO a Entity
        ReservationItem item = mapper.toEntity(dto);
        
        // 5. Asignar las entidades manualmente
        item.setProducto(producto);
        item.setStore(store);
        item.setReservation(reservation); // ✅ Asignar reserva
        
        // 6. Asignar priceSnapshot y status si faltan
        if (item.getPriceSnapshot() == null) {
            item.setPriceSnapshot(producto.getPrecio());
        }
        if (item.getStatus() == null) {
            item.setStatus("PENDING");
        }
        
        // 7. Guardar la ENTIDAD directamente (✅ CORRECCIÓN: Usamos el CRUD repo para guardar la entidad poblada)
        ReservationItem savedItem = reservationItemCrudRepository.save(item);
        
        // 8. Retornar DTO mapeado desde la entidad guardada
        return mapper.toDto(savedItem);
    }

    @Override
    public Optional<ReservationItemDto> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<ReservationItemDto> getByReservation(Long reservationId) {
        return repository.findByReservationId(reservationId);
    }

    @Override
    public List<ReservationItemDto> getByStore(Long storeId) {
        return repository.findByStoreId(storeId);
    }

    @Override
    public List<ReservationItemDto> getByStoreAndStatus(Long storeId, String status) {
        return repository.findByStoreIdAndStatus(storeId, status);
    }

    @Override
    public ReservationItemDto update(ReservationItemDto dto) {
        return repository.save(dto);
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }
}