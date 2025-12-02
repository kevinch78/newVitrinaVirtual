package com.vitrina.vitrinaVirtual.infraestructura.crud_interface;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vitrina.vitrinaVirtual.infraestructura.entity.ReservationItem;

public interface ReservationItemCrudRepository extends JpaRepository<ReservationItem, Long> {

    List<ReservationItem> findByReservationId(Long reservationId);

    List<ReservationItem> findByStoreId(Long storeId);

    List<ReservationItem> findByStoreIdAndStatus(Long storeId, String status);
}
