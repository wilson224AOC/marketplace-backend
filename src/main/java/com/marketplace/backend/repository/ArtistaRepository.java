package com.marketplace.backend.repository;

import com.marketplace.backend.model.Artista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ArtistaRepository extends JpaRepository<Artista, Integer> {
    Optional<Artista> findByUsuarioUsuarioId(Integer usuarioId);
    boolean existsByDni(String dni);
}