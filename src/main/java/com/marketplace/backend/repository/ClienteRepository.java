package com.marketplace.backend.repository;

import com.marketplace.backend.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findByUsuarioUsuarioId(Integer usuarioId);
    boolean existsByDni(String dni);
}