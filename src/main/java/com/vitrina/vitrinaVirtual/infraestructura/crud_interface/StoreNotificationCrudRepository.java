package com.vitrina.vitrinaVirtual.infraestructura.crud_interface;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vitrina.vitrinaVirtual.infraestructura.entity.StoreNotification;

public interface StoreNotificationCrudRepository extends JpaRepository<StoreNotification, Long> {

    List<StoreNotification> findByStoreId(Long storeId);

    // Corregido: El nombre del campo es 'isRead', no 'read'.
    List<StoreNotification> findByStoreIdAndIsReadFalse(Long storeId);

    // Corregido: se usa isRead en la consulta y se añade @Param para claridad.
    @Query("SELECT n FROM StoreNotification n WHERE n.store.id = :storeId AND n.isRead = false")
    List<StoreNotification> findUnreadByStoreId(@Param("storeId") Long storeId);

    // Corregido: se usa isRead en la consulta y se añade @Param para claridad.
    @Modifying
    @Query("UPDATE StoreNotification n SET n.isRead = true WHERE n.id = :id")
    void markAsRead(@Param("id") Long id);
}
