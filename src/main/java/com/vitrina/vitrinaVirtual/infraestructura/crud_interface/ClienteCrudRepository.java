package com.vitrina.vitrinaVirtual.infraestructura.crud_interface;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vitrina.vitrinaVirtual.infraestructura.entity.Cliente;



public interface ClienteCrudRepository extends JpaRepository<Cliente, Long> {

    boolean existsByUsuarioIdUsuario(Long idUsuario);
    Optional<Cliente> findByUsuario_IdUsuario(Long usuarioId);


}


