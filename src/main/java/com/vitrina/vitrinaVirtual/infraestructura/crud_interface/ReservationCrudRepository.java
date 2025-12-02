package com.vitrina.vitrinaVirtual.infraestructura.crud_interface;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vitrina.vitrinaVirtual.infraestructura.entity.Reservation;

public interface ReservationCrudRepository extends JpaRepository<Reservation, Long> {

    // Obtener reservas por cliente
    List<Reservation> findByClientId(Long clientId);

    // Obtener reservas por estado
    List<Reservation> findByStatus(String status);

    List<Reservation> findByStoreId(Long storeId);

}
